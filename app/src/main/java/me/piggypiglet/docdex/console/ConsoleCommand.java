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

    private final String prefix;
    private final String description;

    protected ConsoleCommand(@NotNull final String prefix, @NotNull final String description) {
        this.prefix = prefix;
        this.description = description;
    }

    public abstract void execute();

    @NotNull
    public String getPrefix() {
        return prefix;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    protected static void msg(@NotNull final Object message) {
        LOGGER.info(message.toString());
    }
}
