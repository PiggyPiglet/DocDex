package me.piggypiglet.docdex.config;

import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class PterodactylConfig {
    private String link;
    private String token;
    private String server;
    private String directory;

    @NotNull
    public String getLink() {
        return link;
    }

    @NotNull
    public String getToken() {
        return token;
    }

    @NotNull
    public String getServer() {
        return server;
    }

    @NotNull
    public String getDirectory() {
        return directory;
    }
}
