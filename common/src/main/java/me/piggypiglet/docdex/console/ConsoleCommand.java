package me.piggypiglet.docdex.console;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class ConsoleCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger("Command Output");
    private static final Pattern ARGUMENT_DELIMITER = Pattern.compile(" ");

    private final String match;
    private final String description;

    protected ConsoleCommand(@NotNull final String match, @NotNull final String description) {
        this.match = match;
        this.description = description;
    }

    protected void execute() {}

    protected void execute(@NotNull final List<String> args) {}

    public final void run(@NotNull final String text) {
        execute();
        execute(Arrays.asList(ARGUMENT_DELIMITER.split(text.trim())));
    }

    @NotNull
    public String getMatch() {
        return match;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    protected static void msg(@NotNull final Object message) {
        LOGGER.info("{}", message);
    }
}
