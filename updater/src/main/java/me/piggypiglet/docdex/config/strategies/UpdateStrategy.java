package me.piggypiglet.docdex.config.strategies;

import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public interface UpdateStrategy {
    @NotNull
    String getPath();
}
