package me.piggypiglet.docdex.documentation.index.algorithm;

import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public interface StringDistance {
    double calculate(final byte @NotNull [] x, final byte @NotNull [] y);
}
