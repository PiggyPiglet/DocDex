package me.piggypiglet.docdex.pterodactyl.requests.implementations.command;

import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class CommandPayload {
    private final String command;

    public CommandPayload(@NotNull final String command) {
        this.command = command;
    }
}
