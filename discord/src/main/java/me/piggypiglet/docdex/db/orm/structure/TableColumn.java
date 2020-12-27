package me.piggypiglet.docdex.db.orm.structure;

import me.piggypiglet.docdex.db.orm.structure.objects.SqlDataStructures;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TableColumn {
    private final String name;
    private final SqlDataStructures dataStructure;

    public TableColumn(@NotNull final String name, @NotNull final SqlDataStructures dataStructure) {
        this.name = name;
        this.dataStructure = dataStructure;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public SqlDataStructures getDataStructure() {
        return dataStructure;
    }
}
