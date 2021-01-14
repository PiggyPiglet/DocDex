package me.piggypiglet.docdex;

import com.google.inject.Inject;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.console.ConsoleCommand;
import me.piggypiglet.docdex.pterodactyl.PterodactylManager;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TestCommand extends ConsoleCommand {
    private final Config config;
    private final PterodactylManager pterodactylManager;

    @Inject
    public TestCommand(@NotNull final Config config, @NotNull final PterodactylManager pterodactylManager) {
        super("test", "");
        this.config = config;
        this.pterodactylManager = pterodactylManager;
    }

    @Override
    protected void execute() {
        pterodactylManager.deleteJavadocsAndStop(config.getJavadocs());
    }
}
