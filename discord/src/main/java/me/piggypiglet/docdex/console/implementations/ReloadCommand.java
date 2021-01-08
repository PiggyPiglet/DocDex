package me.piggypiglet.docdex.console.implementations;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bot.registerables.JDAPresenceRegisterable;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.console.ConsoleCommand;
import me.piggypiglet.docdex.file.FileManager;
import me.piggypiglet.docdex.file.annotations.File;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ReloadCommand extends ConsoleCommand {
    private final FileManager fileManager;
    private final JDAPresenceRegisterable presenceRegisterable;

    @Inject
    public ReloadCommand(@NotNull final FileManager fileManager, @NotNull final JDAPresenceRegisterable presenceRegisterable) {
        super("reload", "reload the config.");
        this.fileManager = fileManager;
        this.presenceRegisterable = presenceRegisterable;
    }

    @Override
    public void execute() {
        final File file = Config.class.getAnnotation(File.class);
        fileManager.loadFile(Config.class, file.internalPath(), file.externalPath());
        presenceRegisterable.execute();
        msg("Reloaded config.json & applied values.");
    }
}
