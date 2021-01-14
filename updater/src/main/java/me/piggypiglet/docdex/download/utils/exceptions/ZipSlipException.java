package me.piggypiglet.docdex.download.utils.exceptions;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ZipSlipException extends IOException {
    public ZipSlipException(@NotNull final String message) {
        super(message);
    }
}
