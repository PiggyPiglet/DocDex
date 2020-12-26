package me.piggypiglet.docdex.db.orm.structure.factory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.db.orm.structure.TableField;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import me.piggypiglet.docdex.db.orm.structure.generation.StructureGenerator;
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

    private final Set<StructureGenerator> adapters;

    @Inject
    public TableStructureFactory(@NotNull @Named("structure adapters") final Set<StructureGenerator> adapters) {
        this.adapters = adapters;
    }

    @NotNull
    public TableStructure from(@NotNull final Class<?> clazz, @NotNull final String name,
                               @NotNull final TableField identifier) {
        return builder(clazz, name, identifier).build();
    }

    @NotNull
    public TableStructureBuilder builder(@NotNull final Class<?> clazz, @NotNull final String name,
                                         @NotNull final TableField identifier) {
        return builder(clazz, name, identifier, false);
    }

    @NotNull
    public TableStructureBuilder builder(@NotNull final Class<?> clazz, @NotNull final String name,
                                         @NotNull final TableField identifier, final boolean intermediate) {
        final TableStructureBuilder builder = TableStructureBuilder.builder(intermediate);
        builder.clazz(clazz);
        builder.name(name);
        builder.identifier(identifier);

        builder.subStructures(Arrays.stream(clazz.getDeclaredFields())
                .map(field -> {
                    if (StructureGenerator.checkGenericType(field.getGenericType())) {
                        builder.columns(TableField.of(field));
                        return null;
                    }

                    final Optional<StructureGenerator> optionalAdapter = adapters.stream()
                            .filter(adapter -> adapter.shouldAdapt(field))
                            .findAny();

                    if (optionalAdapter.isEmpty()) {
                        return null;
                    }

                    return optionalAdapter.get().generate(field, name, this);
                })
                .filter(Objects::nonNull)
                .filter(structure -> !structure.isIntermediate())
                .peek(structure -> structure.columns(recursionFormat(identifier, 1)))
                .peek(structure -> formatSubStructures(structure, 1, identifier))
                .collect(Collectors.toSet()));

        return builder;
    }

    @NotNull
    private static Stream<TableStructureBuilder> formatSubStructures(@NotNull final TableStructureBuilder tableStructure, final int level,
                                                                     @NotNull final TableField identifier) {
        final Set<TableStructureBuilder> subs = tableStructure.getSubStructures();
        subs.forEach(subStructure -> subStructure.columns(recursionFormat(identifier, level + 1)));

        return Stream.concat(subs.stream(), subs.stream().flatMap(subStructure -> formatSubStructures(subStructure, level + 1, identifier)));
    }

    @NotNull
    private static TableField recursionFormat(@NotNull final TableField field, final int level) {
        if (field.isField()) {
            return TableField.of(Objects.requireNonNull(field.getField()), level);
        }

        return TableField.of(Objects.requireNonNull(field.getString()), level);
    }
}
