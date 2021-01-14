package me.piggypiglet.docdex.bootstrap;

import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.bootstrap.registerables.UpdateRegisterable;
import me.piggypiglet.docdex.config.app.registerables.AppConfigRegisterable;
import me.piggypiglet.docdex.download.registerables.DownloadersRegisterable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class UpdaterBootstrap extends DocDexBootstrap {
    @NotNull
    @Override
    protected List<Class<? extends Registerable>> provideRegisterables() {
        return List.of(
                AppConfigRegisterable.class,
                DownloadersRegisterable.class,
                UpdateRegisterable.class
        );
    }
}
