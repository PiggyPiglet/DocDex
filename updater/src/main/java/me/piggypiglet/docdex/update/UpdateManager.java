package me.piggypiglet.docdex.update;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.config.UpdaterJavadoc;
import me.piggypiglet.docdex.config.app.AppConfig;
import me.piggypiglet.docdex.file.FileManager;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class UpdateManager {
    private final Config config;
    private final AppConfig appConfig;
    private final FileManager fileManager;

    @Inject
    public UpdateManager(@NotNull final Config config, @NotNull final AppConfig appConfig,
                         @NotNull final FileManager fileManager) {
        this.config = config;
        this.appConfig = appConfig;
        this.fileManager = fileManager;
    }

    public void update(@NotNull final UpdaterJavadoc javadoc) {
        final Javadoc newJavadoc = new Javadoc(javadoc.getNames(), javadoc.getLink(), javadoc.getActualLink());
        appConfig.getJavadocs().remove(newJavadoc);
        appConfig.getJavadocs().add(newJavadoc);
    }

    public void applyUpdates() {
        fileManager.saveFile(config.getPterodactyl().getDirectory() + "/config.json", appConfig);
//        fileManager.saveFile("/opt/docdex/config.json", appConfig);
    }
}
