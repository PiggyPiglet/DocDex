package me.piggypiglet.docdex.db.orm.structure.adaptation;

import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public interface StructureAdapter {
    boolean shouldAdapt(@NotNull final Class<?> type);

    @NotNull
    TableStructure serialize(@NotNull final Field field, @NotNull final String name,
                             @NotNull final String identifier);
}
