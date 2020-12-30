package me.piggypiglet.docdex.db.dbo.framework;

import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public interface DatabaseObjectCreator<T> {
    @NotNull
    T createInstance();
}
