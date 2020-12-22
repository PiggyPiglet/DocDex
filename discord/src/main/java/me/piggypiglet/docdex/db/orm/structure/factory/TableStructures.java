package me.piggypiglet.docdex.db.orm.structure.factory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import me.piggypiglet.docdex.db.orm.structure.adaptation.StructureAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TableStructures {
    private final Set<StructureAdapter> adapters;

    @Inject
    public TableStructures(@NotNull @Named("structure adapters") final Set<StructureAdapter> adapters) {
        this.adapters = adapters;
    }

    @NotNull
    public TableStructure from(@NotNull final Class<?> clazz, @NotNull final String name,
                               @NotNull final String identifier) {
        return from(clazz, name, identifier, false);
    }

    @NotNull
    public TableStructure from(@NotNull final Class<?> clazz, @NotNull final String name,
                               @NotNull final String identifier, final boolean intermediate) {
        final TableStructureBuilder builder = TableStructureBuilder.builder(intermediate);
        builder.name(name);
        builder.identifier(identifier);

        builder.subStructures(Arrays.stream(clazz.getFields())
                .map(field -> {
                    final Optional<StructureAdapter> optionalAdapter = adapters.stream()
                            .filter(adapter -> adapter.shouldAdapt(field.getType()))
                            .findAny();

                    if (optionalAdapter.isEmpty()) {
                        builder.columns(field.getName());
                        return null;
                    }

                    return optionalAdapter.get().serialize(field, name, identifier);
                })
                .filter(Objects::nonNull)
                .filter(structure -> !structure.isIntermediate())
                .collect(Collectors.toSet()));

        return builder.build();
    }
}
