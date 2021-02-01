package me.piggypiglet.docdex.documentation.index;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.set.hash.TCustomHashSet;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.index.algorithm.Algorithm;
import me.piggypiglet.docdex.documentation.index.array.ArrayHashingStrategy;
import me.piggypiglet.docdex.documentation.index.objects.DocumentedObjectKey;
import me.piggypiglet.docdex.documentation.index.storage.implementations.MongoStorage;
import me.piggypiglet.docdex.documentation.index.utils.StreamUtils;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedObjectResult;
import me.piggypiglet.docdex.documentation.objects.MongoDocumentedObjectFields;
import me.piggypiglet.docdex.documentation.utils.DataUtils;
import me.piggypiglet.docdex.documentation.utils.ParameterTypes;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class DocumentationIndex {
    private static final Pattern PARAMETER_DELIMITER = Pattern.compile(",");

    private final Multimap<Javadoc, byte[]> types = createArrayValueMultimap();
    private final Multimap<Javadoc, byte[]> fqnTypes = createArrayValueMultimap();
    private final Multimap<Javadoc, byte[]> fields = createArrayValueMultimap();
    private final Multimap<Javadoc, byte[]> fqnFields = createArrayValueMultimap();

    private final Map<Javadoc, Multimap<byte[], byte[]>> fullMethods = new ConcurrentHashMap<>();
    private final Map<Javadoc, Multimap<byte[], byte[]>> fullFqnMethods = new ConcurrentHashMap<>();
    private final Map<Javadoc, Multimap<byte[], byte[]>> typeMethods = new ConcurrentHashMap<>();
    private final Map<Javadoc, Multimap<byte[], byte[]>> typeFqnMethods = new ConcurrentHashMap<>();
    private final Map<Javadoc, Multimap<byte[], byte[]>> nameMethods = new ConcurrentHashMap<>();
    private final Map<Javadoc, Multimap<byte[], byte[]>> nameFqnMethods = new ConcurrentHashMap<>();

    @NotNull
    private static <K, V> Multimap<K, V> createArrayValueMultimap() {
        return Multimaps.newSetMultimap(new HashMap<>(), () -> new TCustomHashSet<>(ArrayHashingStrategy.get()));
    }

    @NotNull
    private static <K, V> Multimap<K, V> createArrayKeyValueMultimap() {
        return Multimaps.newSetMultimap(new TCustomHashMap<>(ArrayHashingStrategy.get()), () -> new TCustomHashSet<>(ArrayHashingStrategy.get()));
    }

    private final MongoStorage storage;

    @Inject
    public DocumentationIndex(@NotNull final MongoStorage storage) {
        this.storage = storage;
    }

    public void populate(@NotNull final Javadoc javadoc, @NotNull final Map<DocumentedObjectKey, DocumentedObject> objects) {
        Stream.of(
                fullMethods, fullFqnMethods, typeMethods, typeFqnMethods,
                nameMethods, nameFqnMethods
        ).forEach(map -> map.put(javadoc, createArrayKeyValueMultimap()));

        for (final Map.Entry<DocumentedObjectKey, DocumentedObject> entry : objects.entrySet()) {
            final DocumentedObjectKey key = entry.getKey();
            final byte[] name = toAscii(key.getName());
            final byte[] fqn = toAscii(key.getFqn());
            final DocumentedObject object = entry.getValue();
            final Multimap<Javadoc, byte[]> names;
            final Multimap<Javadoc, byte[]> fqns;

            switch (object.getType()) {
                case CLASS:
                case INTERFACE:
                case ANNOTATION:
                case ENUM:
                    names = types;
                    fqns = fqnTypes;
                    break;

                case CONSTRUCTOR:
                case METHOD:
                    final Map<ParameterTypes, String> params = DataUtils.getParams(object);
                    final byte[] fullParam = toAscii(params.get(ParameterTypes.FULL));
                    final byte[] typeParam = toAscii(params.get(ParameterTypes.TYPE));
                    final byte[] nameParam = toAscii(params.get(ParameterTypes.NAME));

                    fullMethods.get(javadoc).put(name, fullParam);
                    fullFqnMethods.get(javadoc).put(fqn, fullParam);
                    typeMethods.get(javadoc).put(name, typeParam);
                    typeFqnMethods.get(javadoc).put(fqn, typeParam);
                    nameMethods.get(javadoc).put(name, nameParam);
                    nameFqnMethods.get(javadoc).put(fqn, nameParam);
                    continue;

                case FIELD:
                    names = fields;
                    fqns = fqnFields;
                    break;

                default:
                    continue;
            }

            fqns.put(javadoc, fqn);
            names.put(javadoc, name);
        }
    }

    @NotNull
    public List<DocumentedObjectResult> get(@NotNull final Javadoc javadoc, @NotNull String query,
                                            @NotNull final Algorithm algorithm, final int limit) {
        query = query.toLowerCase();
        final Multimap<Javadoc, byte[]> map;

        if (query.contains(".")) {
            if (query.contains("#")) {
                return getMethods(javadoc, query, true, algorithm, limit);
            } else if (query.contains("%")) {
                map = fqnFields;
            } else {
                map = fqnTypes;
            }
        } else if (query.contains("#")) {
            return getMethods(javadoc, query, false, algorithm, limit);
        } else if (query.contains("%")) {
            map = fields;
        } else {
            map = types;
        }

        final MongoDocumentedObjectFields field;

        if (query.contains(".")) {
            field = MongoDocumentedObjectFields.IDENTIFIER;
        } else {
            field = MongoDocumentedObjectFields.NAME;
        }

        if (map.isEmpty()) {
            return Collections.emptyList();
        }

        return getFromStorage(getNames(map.get(javadoc), toAscii(query), algorithm), javadoc, field, limit);
    }

    @NotNull
    private List<DocumentedObjectResult> getMethods(@NotNull final Javadoc javadoc, @NotNull String query,
                                                    final boolean fqn, @NotNull final Algorithm algorithm,
                                                    final int limit) {
        query = query.replace(", ", ",");

        final String methodQueryString;
        final String parameterQueryString;

        if (query.contains("(")) {
            final int openIndex = query.indexOf('(');
            methodQueryString = query.substring(0, openIndex);

            if (query.endsWith(")")) {
                final int closingIndex = query.lastIndexOf(')');
                parameterQueryString = query.substring(openIndex, closingIndex);
            } else {
                parameterQueryString = query.substring(openIndex);
            }
        } else {
            methodQueryString = query;
            parameterQueryString = "";
        }

        final boolean full = parameterQueryString.isBlank() || Arrays.stream(PARAMETER_DELIMITER.split(parameterQueryString))
                .map(String::trim)
                .anyMatch(parameter -> parameter.contains(" "));
        final byte[] methodQuery = toAscii(methodQueryString);
        final byte[] parameterQuery = toAscii(parameterQueryString);

        final Multimap<byte[], byte[]> fullMethods = (fqn ? fullFqnMethods : this.fullMethods).get(javadoc);
        final Multimap<byte[], byte[]> typeMethods = (fqn ? typeFqnMethods : this.typeMethods).get(javadoc);
        final Multimap<byte[], byte[]> nameMethods = (fqn ? nameFqnMethods : this.nameMethods).get(javadoc);

        if (full) {
            return getFromStorage(
                    toFormattedMethodNames(getMethodNames(fullMethods, methodQuery, parameterQuery, algorithm)),
                    javadoc, DataUtils.fromParameterType(ParameterTypes.FULL, fqn), limit
            );
        }

        final List<Map.Entry<byte[], byte[]>> names = getMethodNames(nameMethods, methodQuery, parameterQuery, algorithm);

        if (names.size() == 1) {
            return getFromStorage(toFormattedMethodNames(names), javadoc, DataUtils.fromParameterType(ParameterTypes.NAME, fqn), limit);
        }

        final List<Map.Entry<byte[], byte[]>> types = getMethodNames(typeMethods, methodQuery, parameterQuery, algorithm);

        if (types.size() == 1) {
            return getFromStorage(toFormattedMethodNames(types), javadoc, DataUtils.fromParameterType(ParameterTypes.TYPE, fqn), limit);
        }

        final List<Map.Entry<ParameterTypes, String>> keys = Stream.concat(
                types.stream().map(name -> Map.entry(ParameterTypes.TYPE, name)),
                names.stream().map(name -> Map.entry(ParameterTypes.NAME, name))
        )
                .parallel()
                .filter(StreamUtils.distinctByKey(entry -> new String(entry.getValue().getKey()) + '(' + new String(entry.getValue().getValue()) + ')'))
                .sorted(Comparator.comparingDouble(object -> {
                    final Map.Entry<byte[], byte[]> name = object.getValue();
                    final double methodRatio = algorithm.calculate(name.getKey(), methodQuery);
                    final double parameterRatio = algorithm.calculate(name.getValue(), parameterQuery);

                    return methodRatio + parameterRatio;
                }))
                .map(entry -> Map.entry(entry.getKey(), new String(entry.getValue().getKey()) + '(' + new String(entry.getValue().getValue()) + ')'))
                .collect(Collectors.toList());
        final List<DocumentedObjectResult> results = new ArrayList<>();

        for (final Map.Entry<ParameterTypes, String> key : keys) {
            final DocumentedObjectResult result = storage.get(javadoc, Map.of(DataUtils.fromParameterType(key.getKey(), fqn).getName(), key.getValue()))
                    .map(documentedObject -> new DocumentedObjectResult(key.getValue(), documentedObject))
                    .orElse(null);

            if (result == null || results.stream().map(DocumentedObjectResult::getObject).anyMatch(result.getObject()::equals)) {
                continue;
            }

            results.add(result);

            if (results.size() == limit) {
                break;
            }
        }

        return results;
    }

    @NotNull
    private List<byte[]> getNames(@NotNull final Collection<byte[]> collection, final byte @NotNull [] query,
                                  @NotNull final Algorithm algorithm) {
        if (collection.contains(query)) {
            return List.of(query);
        }

        return StreamUtils.orderByAlgorithm(collection.stream(), query, algorithm)
                .collect(Collectors.toList());
    }

    @NotNull
    private List<Map.Entry<byte[], byte[]>> getMethodNames(@NotNull final Multimap<byte[], byte[]> map, final byte @NotNull [] methodQuery,
                                                           final byte @NotNull [] parameterQuery, @NotNull final Algorithm algorithm) {
        final List<byte[]> methods = getNames(map.keySet(), methodQuery, algorithm);
        final List<Map.Entry<byte[], byte[]>> results = new ArrayList<>();

        for (final byte[] method : methods) {
            final List<byte[]> parameterResults = getNames(map.get(method), parameterQuery, algorithm);

            for (final byte[] parameterResult : parameterResults) {
                results.add(Map.entry(method, parameterResult));
            }
        }

        return results;
    }

    @NotNull
    private static List<byte[]> toFormattedMethodNames(@NotNull final List<Map.Entry<byte[], byte[]>> methods) {
        return methods.stream()
                .map(entry -> formatName(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private static byte @NotNull [] formatName(final byte @NotNull [] method, final byte @NotNull [] parameters) {
        final int methodLength = method.length;
        final int parametersLength = parameters.length;
        final int resultLength = methodLength + parametersLength + 2;

        final byte[] result = new byte[resultLength];
        System.arraycopy(method, 0, result, 0, methodLength);
        result[methodLength] = '(';
        System.arraycopy(parameters, 0, result, methodLength + 1, parametersLength);
        result[resultLength - 1] = ')';

        return result;
    }

    @NotNull
    private List<DocumentedObjectResult> getFromStorage(@NotNull final List<byte[]> names, @NotNull final Javadoc javadoc,
                                                        @NotNull final MongoDocumentedObjectFields field, final int limit) {
        final List<String> stringNames = names.stream()
                .map(String::new)
                .collect(Collectors.toList());
        final List<DocumentedObjectResult> results = new ArrayList<>();

        for (final String name : stringNames) {
            final DocumentedObjectResult result = storage.get(javadoc, Map.of(field.getName(), name))
                    .map(documentedObject -> new DocumentedObjectResult(name, documentedObject))
                    .orElse(null);

            if (result == null || results.stream().map(DocumentedObjectResult::getObject).anyMatch(result.getObject()::equals)) {
                continue;
            }

            results.add(result);

            if (results.size() == limit) {
                break;
            }
        }

        return results;
    }

    private static byte @NotNull [] toAscii(@NotNull final String string) {
        return string.getBytes(StandardCharsets.US_ASCII);
    }
}
