package me.piggypiglet.docdex.config.app.registerables;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.config.app.AppConfig;
import me.piggypiglet.docdex.file.FileManager;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class AppConfigRegisterable extends Registerable {
    private final Map<Class<?>, Object> fileObjects;
    private final FileManager fileManager;
    private final AppConfig appConfig;
    private final Config config;

    @Inject
    public AppConfigRegisterable(@NotNull @Named("files") final Map<Class<?>, Object> fileObjects, @NotNull final FileManager fileManager,
                                 @NotNull final AppConfig appConfig, @NotNull final Config config) {
        this.fileObjects = fileObjects;
        this.fileManager = fileManager;
        this.appConfig = appConfig;
        this.config = config;
    }

    @Override
    public void execute(final @NotNull Injector injector) {
        fileObjects.put(AppConfig.class, appConfig);
        fileManager.setGson(fileManager.generateBuilder(fileObjects).create());

        fileManager.loadFile(AppConfig.class, "/appconfig.json", config.getPterodactyl().getDirectory() + "/config.json");
//        fileManager.loadFile(AppConfig.class, "/appconfig.json", "/opt/docdex/config.json");
    }
}
