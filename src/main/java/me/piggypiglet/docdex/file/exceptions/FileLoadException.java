package me.piggypiglet.docdex.file.exceptions;

import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class FileLoadException extends RuntimeException {
    public FileLoadException(@NotNull final Exception exception) {
        super(exception);
    }
}
