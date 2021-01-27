package me.piggypiglet.docdex.documentation.index;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.index.algorithm.Algorithm;
import me.piggypiglet.docdex.documentation.index.algorithm.AlgorithmOption;
import me.piggypiglet.docdex.documentation.index.objects.DocumentedObjectKey;
import me.piggypiglet.docdex.documentation.index.storage.implementations.MongoStorage;
import me.piggypiglet.docdex.documentation.index.utils.StreamUtils;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedObjectResult;
import me.piggypiglet.docdex.documentation.objects.MongoDocumentedObjectFields;
import me.piggypiglet.docdex.documentation.utils.DataUtils;
import me.piggypiglet.docdex.documentation.utils.ParameterTypes;
import org.jetbrains.annotations.NotNull;

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

    private final Multimap<Javadoc, String> types = HashMultimap.create();
    private final Multimap<Javadoc, String> fqnTypes = HashMultimap.create();
    private final Multimap<Javadoc, String> fields = HashMultimap.create();
    private final Multimap<Javadoc, String> fqnFields = HashMultimap.create();

    private final Map<Javadoc, Multimap<String, String>> fullMethods = new ConcurrentHashMap<>();
    private final Map<Javadoc, Multimap<String, String>> fullFqnMethods = new ConcurrentHashMap<>();
    private final Map<Javadoc, Multimap<String, String>> typeMethods = new ConcurrentHashMap<>();
    private final Map<Javadoc, Multimap<String, String>> typeFqnMethods = new ConcurrentHashMap<>();
    private final Map<Javadoc, Multimap<String, String>> nameMethods = new ConcurrentHashMap<>();
    private final Map<Javadoc, Multimap<String, String>> nameFqnMethods = new ConcurrentHashMap<>();

    private final MongoStorage storage;

    @Inject
    public DocumentationIndex(@NotNull final MongoStorage storage) {
        this.storage = storage;
    }

    public void populate(@NotNull final Javadoc javadoc, @NotNull final Map<DocumentedObjectKey, DocumentedObject> objects) {
        Stream.of(
                fullMethods, fullFqnMethods, typeMethods, typeFqnMethods,
                nameMethods, nameFqnMethods
        ).forEach(map -> map.put(javadoc, HashMultimap.create()));

        for (final Map.Entry<DocumentedObjectKey, DocumentedObject> entry : objects.entrySet()) {
            final DocumentedObjectKey key = entry.getKey();
            final String name = key.getName();
            final String fqn = key.getFqn();
            final DocumentedObject object = entry.getValue();
            final Multimap<Javadoc, String> names;
            final Multimap<Javadoc, String> fqns;

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
                    final String fullParam = params.get(ParameterTypes.FULL);
                    final String typeParam = params.get(ParameterTypes.TYPE);
                    final String nameParam = params.get(ParameterTypes.NAME);

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
                                            @NotNull final Algorithm algorithm, @NotNull final AlgorithmOption algorithmOption,
                                            final int limit) {
        query = query.toLowerCase();
        final Multimap<Javadoc, String> map;

        if (query.contains(".")) {
            if (query.contains("#")) {
                return getMethods(javadoc, query, true, algorithm, algorithmOption, limit);
            } else if (query.contains("%")) {
                map = fqnFields;
            } else {
                map = fqnTypes;
            }
        } else if (query.contains("#")) {
            return getMethods(javadoc, query, false, algorithm, algorithmOption, limit);
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

        return getFromStorage(getNames(map.get(javadoc), query, algorithm, algorithmOption), javadoc, field, limit);
    }

    @NotNull
    private List<DocumentedObjectResult> getMethods(@NotNull final Javadoc javadoc, @NotNull String query,
                                                    final boolean fqn, @NotNull final Algorithm algorithm,
                                                    @NotNull final AlgorithmOption algorithmOption, final int limit) {
        query = query.replace(", ", ",");

        final String methodQuery;
        final String parameterQuery;

        if (query.contains("(")) {
            final int openIndex = query.indexOf('(');
            methodQuery = query.substring(0, openIndex);

            if (query.endsWith(")")) {
                final int closingIndex = query.lastIndexOf(')');
                parameterQuery = query.substring(openIndex, closingIndex);
            } else {
                parameterQuery = query.substring(openIndex);
            }
        } else {
            methodQuery = query;
            parameterQuery = "";
        }

        final boolean full = parameterQuery.isBlank() || Arrays.stream(PARAMETER_DELIMITER.split(parameterQuery))
                .map(String::trim)
                .anyMatch(parameter -> parameter.contains(" "));

        final Multimap<String, String> fullMethods = (fqn ? fullFqnMethods : this.fullMethods).get(javadoc);
        final Multimap<String, String> typeMethods = (fqn ? typeFqnMethods : this.typeMethods).get(javadoc);
        final Multimap<String, String> nameMethods = (fqn ? nameFqnMethods : this.nameMethods).get(javadoc);

        if (full) {
            return getFromStorage(
                    toFormattedMethodNames(getMethodNames(fullMethods, methodQuery, parameterQuery, algorithm, algorithmOption)),
                    javadoc, DataUtils.fromParameterType(ParameterTypes.FULL, fqn), limit
            );
        }

        final List<Map.Entry<String, String>> names = getMethodNames(nameMethods, methodQuery, parameterQuery, algorithm, algorithmOption);

        if (names.size() == 1) {
            return getFromStorage(toFormattedMethodNames(names), javadoc, DataUtils.fromParameterType(ParameterTypes.NAME, fqn), limit);
        }

        final List<Map.Entry<String, String>> types = getMethodNames(typeMethods, methodQuery, parameterQuery, algorithm, algorithmOption);

        if (types.size() == 1) {
            return getFromStorage(toFormattedMethodNames(types), javadoc, DataUtils.fromParameterType(ParameterTypes.TYPE, fqn), limit);
        }

        final List<Map.Entry<ParameterTypes, String>> keys = Stream.concat(
                types.stream().map(name -> Map.entry(ParameterTypes.TYPE, name)),
                names.stream().map(name -> Map.entry(ParameterTypes.NAME, name))
        )
                .parallel()
                .filter(StreamUtils.distinctByKey(entry -> entry.getValue().getKey() + '(' + entry.getValue().getValue() + ')'))
                .sorted(Comparator.comparingDouble(object -> {
                    final Map.Entry<String, String> name = object.getValue();
                    final double methodRatio = algorithm.calculate(name.getKey(), methodQuery, algorithmOption);
                    final double parameterRatio = parameterQuery.isBlank() ? 0 : algorithm.calculate(name.getValue(), parameterQuery, algorithmOption);

                    return methodRatio + parameterRatio;
                }))
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getKey() + '(' + entry.getValue().getValue() + ')'))
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
    private List<String> getNames(@NotNull final Collection<String> collection, @NotNull final String query,
                                  @NotNull final Algorithm algorithm, @NotNull final AlgorithmOption algorithmOption) {
        if (collection.contains(query)) {
            return List.of(query);
        }

        return StreamUtils.orderByAlgorithm(collection.parallelStream(), query, algorithm, algorithmOption)
                .collect(Collectors.toList());
    }

    @NotNull
    private List<Map.Entry<String, String>> getMethodNames(@NotNull final Multimap<String, String> map, @NotNull final String methodQuery,
                                                           @NotNull final String parameterQuery, @NotNull final Algorithm algorithm,
                                                           @NotNull final AlgorithmOption algorithmOption) {
        final List<String> methods = getNames(map.keySet(), methodQuery, algorithm, algorithmOption);
        final List<Map.Entry<String, String>> results = new ArrayList<>();

        for (final String method : methods) {
            final List<String> parameterResults = getNames(map.get(method), parameterQuery, algorithm, algorithmOption);

            for (final String parameterResult : parameterResults) {
                results.add(Map.entry(method, parameterResult));
            }
        }

        return results;
    }

    @NotNull
    private static List<String> toFormattedMethodNames(@NotNull final List<Map.Entry<String, String>> methods) {
        return methods.stream()
                .map(entry -> entry.getKey() + '(' + entry.getValue() + ')')
                .collect(Collectors.toList());
    }

    @NotNull
    private List<DocumentedObjectResult> getFromStorage(@NotNull final List<String> names, @NotNull final Javadoc javadoc,
                                                        @NotNull final MongoDocumentedObjectFields field, final int limit) {
        final List<DocumentedObjectResult> results = new ArrayList<>();

        for (final String name : names) {
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
}
