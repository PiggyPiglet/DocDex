package me.piggypiglet.docdex.console;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class ConsoleCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger("Command Output");

    private final String match;
    private final String description;

    protected ConsoleCommand(@NotNull final String match, @NotNull final String description) {
        this.match = match;
        this.description = description;
    }

    public abstract void execute();

    @NotNull
    public String getMatch() {
        return match;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    protected static void msg(@NotNull final Object message) {
        LOGGER.info(message.toString());
    }
}
