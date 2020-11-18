package me.piggypiglet.docdex.commands.implementations;

import com.google.inject.Inject;
import me.piggypiglet.docdex.commands.Command;
import me.piggypiglet.docdex.config.Config;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TestCommand extends Command {
    private final Config config;

    @Inject
    public TestCommand(@NotNull final Config config) {
        super("test", "test");
        this.config = config;
    }

    @Override
    public void execute() {
        msg(config.getPort());
    }
}
