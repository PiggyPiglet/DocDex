package me.piggypiglet.docdex.console.implementations;

import com.google.inject.Inject;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.console.ConsoleCommand;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TestCommand extends ConsoleCommand {
    private final Config config;

    @Inject
    public TestCommand(@NotNull final Config config) {
        super("test", "");
        this.config = config;
    }

    @Override
    public void execute() {
        msg(config.getPresence());
    }
}
