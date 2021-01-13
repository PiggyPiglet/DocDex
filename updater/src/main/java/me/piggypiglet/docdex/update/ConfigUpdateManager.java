package me.piggypiglet.docdex.update;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.config.app.AppConfig;
import me.piggypiglet.docdex.file.FileManager;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class ConfigUpdateManager {
    private final Config config;
    private final AppConfig appConfig;
    private final FileManager fileManager;

    @Inject
    public ConfigUpdateManager(@NotNull final Config config, @NotNull final AppConfig appConfig,
                               @NotNull final FileManager fileManager) {
        this.config = config;
        this.appConfig = appConfig;
        this.fileManager = fileManager;
    }

    public void update() {
        config.getJavadocs().forEach(javadoc ->
                appConfig.getJavadocs().add(new Javadoc(javadoc.getNames(), javadoc.getLink(), javadoc.getActualLink())));

        fileManager.saveFile(config.getAppConfigPath(), appConfig);
    }
}
