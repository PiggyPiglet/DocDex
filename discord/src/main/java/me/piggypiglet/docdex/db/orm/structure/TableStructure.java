package me.piggypiglet.docdex.db.orm.structure;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TableStructure {
    private final Class<?> clazz;
    private final String name;
    private final Set<TableColumn> identifiers;
    private final Set<TableColumn> columns;

    public TableStructure(@NotNull final Class<?> clazz, @NotNull final String name,
                          @NotNull final Set<TableColumn> identifiers, @NotNull final Set<TableColumn> columns) {
        this.clazz = clazz;
        this.name = name;
        this.identifiers = identifiers;
        this.columns = columns;
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
    public Set<TableColumn> getIdentifiers() {
        return identifiers;
    }

    @NotNull
    public Set<TableColumn> getColumns() {
        return columns;
    }
}
