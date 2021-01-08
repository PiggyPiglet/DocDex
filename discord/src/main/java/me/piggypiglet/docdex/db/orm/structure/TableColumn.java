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
    private final int length;

    public TableColumn(@NotNull final String name, @NotNull final SqlDataStructures dataStructure,
                       final int length) {
        this.name = name;
        this.dataStructure = dataStructure;
        this.length = length;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public SqlDataStructures getDataStructure() {
        return dataStructure;
    }

    public int getLength() {
        return length;
    }
}
