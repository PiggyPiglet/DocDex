package me.piggypiglet.docdex.documentation.index;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class DocumentationIndex {
    private final Table<String, String, DocumentedObject> docs = HashBasedTable.create();

    @Inject
    public DocumentationIndex(@NotNull @Named("documentation") final Map<Javadoc, Set<DocumentedObject>> documentation) {
        documentation.forEach((javadoc, objects) ->
                javadoc.getNames().forEach(name -> objects.forEach(object -> docs.put(name.toLowerCase(), object.getName().toLowerCase(), object))));
    }

    @Nullable
    public DocumentedObject get(@NotNull final String javadoc, @NotNull final String query) {
        if (docs.isEmpty()) {
            return null;
        }

        if (!docs.containsRow(javadoc)) {
            return null;
        }

        if (docs.row(javadoc).isEmpty()) {
            return null;
        }

        final DocumentedObject object = docs.get(javadoc, query);

        if (object != null) {
            return object;
        }

        //noinspection OptionalGetWithoutIsPresent
        return docs.row(javadoc).values().stream()
                .max(Comparator.comparingInt(element -> FuzzySearch.weightedRatio(element.getName(), query)))
                .get();
    }
}
