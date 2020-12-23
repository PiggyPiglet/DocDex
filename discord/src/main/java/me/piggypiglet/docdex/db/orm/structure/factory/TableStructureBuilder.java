package me.piggypiglet.docdex.db.orm.structure.factory;

import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TableStructureBuilder {
    private final boolean intermediate;
    private Class<?> clazz;
    private String name;
    private String identifier;
    private Set<String> columns = new HashSet<>();
    private Set<TableStructureBuilder> subStructures = new HashSet<>();

    private TableStructureBuilder(final boolean intermediate) {
        this.intermediate = intermediate;
    }

    @NotNull
    public static TableStructureBuilder builder(final boolean intermediate) {
        return new TableStructureBuilder(intermediate);
    }

    @NotNull
    public TableStructureBuilder clazz(@NotNull final Class<?> value) {
        clazz = value;
        return this;
    }

    @NotNull
    public TableStructureBuilder name(@NotNull final String value) {
        name = value;
        return this;
    }

    @NotNull
    public TableStructureBuilder identifier(@NotNull final String value) {
        identifier = value;
        return this;
    }

    @NotNull
    public TableStructureBuilder columns(@NotNull final String @NotNull ... values) {
        Collections.addAll(columns, values);
        return this;
    }

    @NotNull
    public TableStructureBuilder columns(@NotNull final Set<String> values) {
        columns.addAll(values);
        return this;
    }

    @NotNull
    public TableStructureBuilder subStructures(@NotNull final TableStructureBuilder @NotNull ... values) {
        Collections.addAll(subStructures, values);
        return this;
    }

    @NotNull
    public TableStructureBuilder subStructures(@NotNull final Set<TableStructureBuilder> values) {
        subStructures.addAll(values);
        return this;
    }

    @NotNull
    public TableStructure build() {
        return new TableStructure(
                name, identifier, Collections.unmodifiableSet(columns),
                subStructures.stream()
                        .map(TableStructureBuilder::build)
                        .collect(Collectors.toUnmodifiableSet())
        );
    }

    boolean isIntermediate() {
        return intermediate;
    }

    @NotNull
    String getName() {
        return name;
    }

    @NotNull
    String getIdentifier() {
        return identifier;
    }

    @NotNull
    Set<String> getColumns() {
        return columns;
    }

    @NotNull
    Set<TableStructureBuilder> getSubStructures() {
        return subStructures;
    }
}
