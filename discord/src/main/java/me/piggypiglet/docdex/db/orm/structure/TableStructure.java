package me.piggypiglet.docdex.db.orm.structure;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TableStructure {
    private final String name;
    private final String identifier;
    private final Set<String> columns;
    private final Set<TableStructure> subStructures;

    public TableStructure(@NotNull final String name, @NotNull final String identifier,
                          @NotNull @Unmodifiable final Set<String> columns, @NotNull @Unmodifiable final Set<TableStructure> subStructures) {
        this.name = name;
        this.identifier = identifier;
        this.columns = columns;
        this.subStructures = subStructures;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getIdentifier() {
        return identifier;
    }

    @NotNull @Unmodifiable
    public Set<String> getColumns() {
        return columns;
    }

    @NotNull @Unmodifiable
    public Set<TableStructure> getSubStructures() {
        return subStructures;
    }

    @Override
    public String toString() {
        return "TableStructure{" +
                "name='" + name + '\'' +
                ", identifier='" + identifier + '\'' +
                ", columns=" + columns +
                ", subStructures=" + subStructures +
                '}';
    }
}
