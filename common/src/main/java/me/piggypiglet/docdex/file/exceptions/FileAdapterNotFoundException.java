package me.piggypiglet.docdex.file.exceptions;

import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class FileAdapterNotFoundException extends RuntimeException {
    public FileAdapterNotFoundException(@NotNull final String message) {
        super(message);
    }
}
