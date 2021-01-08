package me.piggypiglet.docdex.scanning.exceptions;

import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ScanningException extends RuntimeException {
    public ScanningException(@NotNull final Exception exception) {
        super(exception);
    }
}
