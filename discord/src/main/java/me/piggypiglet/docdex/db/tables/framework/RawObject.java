package me.piggypiglet.docdex.db.tables.framework;

import org.jetbrains.annotations.Nullable;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public interface RawObject {
    boolean actualEquals(@Nullable final Object o);
}
