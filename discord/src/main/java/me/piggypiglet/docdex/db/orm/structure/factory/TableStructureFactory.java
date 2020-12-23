package me.piggypiglet.docdex.db.orm.structure.factory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import me.piggypiglet.docdex.db.orm.structure.adaptation.StructureAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TableStructureFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger("TableStructureFactory");

    private final Set<StructureAdapter> adapters;

    @Inject
    public TableStructureFactory(@NotNull @Named("structure adapters") final Set<StructureAdapter> adapters) {
        this.adapters = adapters;
    }

    @NotNull
    public TableStructure from(@NotNull final Class<?> clazz, @NotNull final String name,
                               @NotNull final String identifier) {
        return builder(clazz, name, identifier).build();
    }

    @NotNull
    public TableStructureBuilder builder(@NotNull final Class<?> clazz, @NotNull final String name,
                                         @NotNull final String identifier) {
        return builder(clazz, name, identifier, false);
    }

    @NotNull
    public TableStructureBuilder builder(@NotNull final Class<?> clazz, @NotNull final String name,
                                         @NotNull final String identifier, final boolean intermediate) {
        final TableStructureBuilder builder = TableStructureBuilder.builder(intermediate);
        builder.name(name);
        builder.identifier(identifier);

        builder.subStructures(Arrays.stream(clazz.getDeclaredFields())
                .map(field -> {
                    if (StructureAdapter.checkGenericType(field.getGenericType())) {
                        builder.columns(field.getName());
                        return null;
                    }

                    final Optional<StructureAdapter> optionalAdapter = adapters.stream()
                            .filter(adapter -> adapter.shouldAdapt(field))
                            .findAny();

                    if (optionalAdapter.isEmpty()) {
                        return null;
                    }

                    return optionalAdapter.get().generate(field, name, this);
                })
                .filter(Objects::nonNull)
                .filter(structure -> !structure.isIntermediate())
                .peek(structure -> structure.columns('$' + identifier))
                .collect(Collectors.toSet()));

        return builder;
    }

    @NotNull
    private static Stream<TableStructure> getSubStructures(@NotNull final TableStructure tableStructure) {
        return tableStructure.getSubStructures().stream()
                .flatMap(TableStructureFactory::getSubStructures);
    }
}
