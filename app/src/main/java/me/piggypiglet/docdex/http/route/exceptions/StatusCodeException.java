package me.piggypiglet.docdex.http.route.exceptions;

import fi.iki.elonen.NanoHTTPD;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class StatusCodeException extends RuntimeException {
    private final NanoHTTPD.Response.Status status;

    public StatusCodeException(@NotNull final NanoHTTPD.Response.Status status, @NotNull final String message) {
        super(message);
        this.status = status;
    }

    @NotNull
    public NanoHTTPD.Response.Status getStatus() {
        return status;
    }
}
