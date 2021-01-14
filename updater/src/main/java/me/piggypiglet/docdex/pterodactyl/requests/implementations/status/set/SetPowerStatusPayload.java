package me.piggypiglet.docdex.pterodactyl.requests.implementations.status.set;

import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class SetPowerStatusPayload {
    private final String signal;

    public SetPowerStatusPayload(@NotNull final String signal) {
        this.signal = signal;
    }
}
