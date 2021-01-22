package me.piggypiglet.docdex.documentation.index.algorithm;

import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@FunctionalInterface
interface AlgorithmFunctionWithoutOption {
    double calculate(@NotNull final String string1, @NotNull final String string2);
}
