package me.piggypiglet.docdex.file.exceptions;

import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class FileSaveException extends RuntimeException {
    public FileSaveException(@NotNull final Exception exception) {
        super(exception);
    }
}
