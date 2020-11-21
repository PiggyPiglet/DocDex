package me.piggypiglet.docdex.documentation.index;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.inject.Singleton;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.method.MethodMetadata;
import me.piggypiglet.docdex.documentation.objects.type.TypeMetadata;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class DocumentationIndex {
    private final Table<String, String, DocumentedObject> types = HashBasedTable.create();
    private final Table<String, String, DocumentedObject> fqnTypes = HashBasedTable.create();
    private final Table<String, String, DocumentedObject> methods = HashBasedTable.create();
    private final Table<String, String, DocumentedObject> fqnMethods = HashBasedTable.create();

    public void populate(@NotNull final Javadoc javadoc, @NotNull final Set<DocumentedObject> objects) {
        javadoc.getNames().forEach(name -> {
            final String javadocName = name.toLowerCase();

            objects.forEach(object -> {
                final Object metadata = object.getMetadata();
                final String objectName = object.getName().toLowerCase();

                switch (object.getType()) {
                    case CLASS:
                    case INTERFACE:
                    case ANNOTATION:
                    case ENUM:
                        types.put(javadocName, objectName, object);
                        fqnTypes.put(javadocName, ((TypeMetadata) metadata).getPackage() + '.' + objectName, object);
                        break;

                    case METHOD:
                        final MethodMetadata methodMetadata = ((MethodMetadata) metadata);
                        final DocumentedObject owner = methodMetadata.getOwner();
                        final TypeMetadata ownerMetadata = ((TypeMetadata) owner.getMetadata());
                        final String ownerName = methodMetadata.getOwner().getName().toLowerCase();

                        methods.put(javadocName, ownerName + '#' + objectName, object);
                        fqnMethods.put(javadocName, ownerMetadata.getPackage() + '.' + ownerName + '#' + objectName, object);
                        break;

                    case FIELD:
                        break;
                    case PARAMETER:
                        break;
                }
            });

            new HashSet<>(methods.row(javadocName).values()).forEach(method -> getInheritedOwners(javadocName, method).forEach(owner -> {
                final String ownerName = owner.getName().toLowerCase();
                final String methodName = method.getName();
                final TypeMetadata ownerMetadata = ((TypeMetadata) owner.getMetadata());

                methods.put(javadocName, ownerName + '#' + methodName, method);
                fqnMethods.put(javadocName, ownerMetadata.getPackage() + '.' + ownerName + '#' + methodName, method);
            }));
        });
    }

    @Nullable
    public DocumentedObject get(@NotNull final String javadoc, @NotNull final String query) {
        final Table<String, String, DocumentedObject> table;

        if (query.contains(".")) {
            if (query.contains("#")) {
                table = fqnMethods;
            } else {
                table = fqnTypes;
            }
        } else if (query.contains("#")) {
            table = methods;
        } else {
            table = types;
        }

        if (table.isEmpty()) {
            return null;
        }

        final String lowerJavadoc = javadoc.toLowerCase();
        final String lowerQuery = query.toLowerCase();

        if (!table.containsRow(lowerJavadoc)) {
            return null;
        }

        if (table.row(lowerJavadoc).isEmpty()) {
            return null;
        }

        final DocumentedObject object = table.get(lowerJavadoc, lowerQuery);

        if (object != null) {
            return object;
        }

        //noinspection OptionalGetWithoutIsPresent
        return table.row(lowerJavadoc).entrySet().stream()
                .max(Comparator.comparingInt(entry -> FuzzySearch.weightedRatio(entry.getKey(), lowerQuery)))
                .map(Map.Entry::getValue)
                .get();
    }

    @NotNull
    private Set<DocumentedObject> getInheritedOwners(@NotNull final String javadoc, @NotNull final DocumentedObject method) {
        return getChildren(javadoc, ((MethodMetadata) method.getMetadata()).getOwner());
    }

    @NotNull
    private Set<DocumentedObject> getChildren(@NotNull final String javadoc, @NotNull final DocumentedObject type) {
        final TypeMetadata typeMetadata = ((TypeMetadata) type.getMetadata());

        final Set<String> subClasses = typeMetadata.getSubClasses();
        final Set<String> subInterfaces = typeMetadata.getSubInterfaces();
        final Set<String> implementingClasses = typeMetadata.getImplementingClasses();

        if (subClasses.isEmpty() && subInterfaces.isEmpty() && implementingClasses.isEmpty()) {
            return Collections.emptySet();
        }

        return Stream.of(
                subClasses,
                subInterfaces,
                implementingClasses
        )
                .flatMap(Set::stream)
                .map(heir -> get(javadoc, heir.toLowerCase()))
                .filter(Objects::nonNull)
                .flatMap(heir -> Stream.concat(Stream.of(heir), getChildren(javadoc, heir).stream()))
                .collect(Collectors.toSet());
    }
}
