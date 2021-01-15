package me.piggypiglet.docdex.config;

import com.google.inject.Singleton;
import me.piggypiglet.docdex.file.annotations.File;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@File(
        internalPath = "/config.json",
        externalPath = "./config.json",
        stopOnFirstCreate = true
) @Singleton
public final class Config {
    private PterodactylConfig pterodactyl;
    private Set<UpdaterJavadoc> javadocs;

    @NotNull
    public PterodactylConfig getPterodactyl() {
        return pterodactyl;
    }

    @NotNull
    public Set<UpdaterJavadoc> getJavadocs() {
        return javadocs;
    }
}
