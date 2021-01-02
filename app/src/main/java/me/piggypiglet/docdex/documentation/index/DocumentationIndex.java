package me.piggypiglet.docdex.documentation.index;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.index.storage.implementations.MongoStorage;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedObjectResult;
import me.piggypiglet.docdex.documentation.objects.MongoDocumentedObjectFields;
import me.piggypiglet.docdex.documentation.utils.DataUtils;
import me.piggypiglet.docdex.documentation.utils.ParameterTypes;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
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

    private final Multimap<Javadoc, String> fullMethods = HashMultimap.create();
    private final Multimap<Javadoc, String> fullFqnMethods = HashMultimap.create();
    private final Multimap<Javadoc, String> typeMethods = HashMultimap.create();
    private final Multimap<Javadoc, String> typeFqnMethods = HashMultimap.create();
    private final Multimap<Javadoc, String> nameMethods = HashMultimap.create();
    private final Multimap<Javadoc, String> nameFqnMethods = HashMultimap.create();

    private final MongoStorage storage;

    @Inject
    public DocumentationIndex(@NotNull final MongoStorage storage) {
        this.storage = storage;
    }

    public void populate(@NotNull final Javadoc javadoc, @NotNull final Map<String, DocumentedObject> objects) {
        for (final Map.Entry<String, DocumentedObject> entry : objects.entrySet()) {
            final String key = entry.getKey();
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
                    final Multimap<Javadoc, String> full;
                    final Multimap<Javadoc, String> type;
                    final Multimap<Javadoc, String> name;

                    if (key.contains(".")) {
                        full = fullFqnMethods;
                        type = typeFqnMethods;
                        name = nameFqnMethods;
                    } else {
                        full = fullMethods;
                        type = typeMethods;
                        name = nameMethods;
                    }

                    full.get(javadoc).add(key + '(' + params.get(ParameterTypes.FULL) + ')');
                    type.get(javadoc).add(key + '(' + params.get(ParameterTypes.TYPE) + ')');
                    name.get(javadoc).add(key + '(' + params.get(ParameterTypes.NAME) + ')');
                    continue;

                case FIELD:
                    names = fields;
                    fqns = fqnFields;
                    break;

                default:
                    continue;
            }

            if (key.contains(".")) {
                fqns.put(javadoc, key);
            } else {
                names.put(javadoc, key);
            }
        }
    }

    @NotNull
    public List<DocumentedObjectResult> get(@NotNull final Javadoc javadoc, @NotNull String query,
                                            final int limit) {
        query = query.toLowerCase();
        final Multimap<Javadoc, String> map;

        if (query.contains(".")) {
            if (query.contains("#")) {
                return getMethods(javadoc, query, true, limit);
            } else if (query.contains("%")) {
                map = fqnFields;
            } else {
                map = fqnTypes;
            }
        } else if (query.contains("#")) {
            return getMethods(javadoc, query, false, limit);
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

        return getFromStorage(get(map.get(javadoc), query, limit), javadoc, field);
    }

    @NotNull
    private List<DocumentedObjectResult> getMethods(@NotNull final Javadoc javadoc, @NotNull String query,
                                                    final boolean fqn, final int limit) {
        if (!query.endsWith(")")) {
            if (!query.contains("(")) {
                query = query + "()";
            } else {
                query = query + ')';
            }
        }

        final String finalQuery = query;

        int splitPoint = finalQuery.lastIndexOf('(');
        final boolean full = Arrays.stream(PARAMETER_DELIMITER.split(finalQuery.substring(splitPoint).replace(')', Character.MIN_VALUE)))
                .map(String::trim)
                .anyMatch(parameter -> parameter.contains(" "));

        final Multimap<Javadoc, String> fullMethods = fqn ? fullFqnMethods : this.fullMethods;
        final Multimap<Javadoc, String> typeMethods = fqn ? typeFqnMethods : this.typeMethods;
        final Multimap<Javadoc, String> nameMethods = fqn ? nameFqnMethods : this.nameMethods;

        if (full) {
            return getFromStorage(get(fullMethods.get(javadoc), finalQuery, limit), javadoc, DataUtils.fromParameterType(ParameterTypes.FULL, fqn));
        }

        final List<String> names = get(nameMethods.get(javadoc), finalQuery, limit);

        if (names.size() == 1) {
            return getFromStorage(names, javadoc, DataUtils.fromParameterType(ParameterTypes.NAME, fqn));
        }

        final List<String> types = get(typeMethods.get(javadoc), finalQuery, limit);

        if (types.size() == 1) {
            return getFromStorage(types, javadoc, DataUtils.fromParameterType(ParameterTypes.TYPE, fqn));
        }

        return Stream.concat(
                types.stream().map(array -> Map.entry(ParameterTypes.TYPE, array)),
                names.stream().map(array -> Map.entry(ParameterTypes.NAME, array))
        )
                .filter(distinctByKey(Map.Entry::getValue))
                .sorted(Collections.reverseOrder(Comparator.comparingInt(object -> FuzzySearch.ratio(object.getValue(), finalQuery))))
                .limit(limit)
                .map(entry -> storage.get(javadoc, Map.of(DataUtils.fromParameterType(entry.getKey(), fqn).getName(), entry.getValue()))
                        .map(documentedObject -> new DocumentedObjectResult(entry.getValue(), documentedObject))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @NotNull
    private List<String> get(@NotNull final Collection<String> collection, @NotNull final String query,
                             final int limit) {
        if (collection.contains(query)) {
            return List.of(query);
        }

        return collection.stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(name -> FuzzySearch.ratio(name, query))))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @NotNull
    private List<DocumentedObjectResult> getFromStorage(@NotNull final List<String> names, @NotNull final Javadoc javadoc,
                                                        @NotNull final MongoDocumentedObjectFields field) {
        return names.stream()
                .map(name -> storage.get(javadoc, Map.of(field.getName(), name))
                        .map(documentedObject -> new DocumentedObjectResult(name, documentedObject))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @NotNull
    private static <T> Predicate<T> distinctByKey(@NotNull final Function<? super T, ?> keyExtractor) {
        final Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
