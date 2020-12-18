package me.piggypiglet.docdex.documentation.index;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.index.data.storage.implementations.MongoStorage;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class DocumentationIndex {
    private final Multimap<Javadoc, String> types = HashMultimap.create();
    private final Multimap<Javadoc, String> fqnTypes = HashMultimap.create();
    private final Multimap<Javadoc, String> methods = HashMultimap.create();
    private final Multimap<Javadoc, String> fqnMethods = HashMultimap.create();
    private final Multimap<Javadoc, String> fields = HashMultimap.create();
    private final Multimap<Javadoc, String> fqnFields = HashMultimap.create();

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
                    names = methods;
                    fqns = fqnMethods;
                    break;

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

    @Nullable
    public DocumentedObject get(@NotNull final Javadoc javadoc, @NotNull final String query) {
        final Multimap<Javadoc, String> map;

        if (query.contains(".")) {
            if (query.contains("#")) {
                map = fqnMethods;
            } else if (query.contains("%")) {
                map = fqnFields;
            } else {
                map = fqnTypes;
            }
        } else if (query.contains("#")) {
            map = methods;
        } else if (query.contains("%")) {
            map = fields;
        } else {
            map = types;
        }

        if (map.isEmpty()) {
            return null;
        }

        final String lowerQuery = query.toLowerCase();
        final DocumentedObject object = map.get(javadoc).contains(lowerQuery) ? storage.get(javadoc, lowerQuery).orElse(null) : null;

        if (object != null) {
            return object;
        }

        return map.get(javadoc).stream()
                .max(Comparator.comparingInt(name -> FuzzySearch.ratio(name, lowerQuery)))
                .flatMap(name -> storage.get(javadoc, name))
                .orElse(null);
    }
}
