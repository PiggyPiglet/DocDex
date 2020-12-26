package me.piggypiglet.docdex.db.orm.structure;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TableStructure {
    private final Class<?> clazz;
    private final String name;
    private final TableField identifier;
    private final Set<TableField> columns;
    private final Set<TableStructure> subStructures;

    public TableStructure(@NotNull final Class<?> clazz, @NotNull final String name, @NotNull final TableField identifier,
                          @NotNull @Unmodifiable final Set<TableField> columns, @NotNull @Unmodifiable final Set<TableStructure> subStructures) {
        this.clazz = clazz;
        this.name = name;
        this.identifier = identifier;
        this.columns = columns;
        this.subStructures = subStructures;
    }

    @NotNull
    public Class<?> getClazz() {
        return clazz;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public TableField getIdentifier() {
        return identifier;
    }

    @NotNull @Unmodifiable
    public Set<TableField> getColumns() {
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
