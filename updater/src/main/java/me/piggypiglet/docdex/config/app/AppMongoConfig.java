package me.piggypiglet.docdex.config.app;

import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class AppMongoConfig {
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public AppMongoConfig(@NotNull final String host, final int port,
                          @NotNull final String database, @NotNull final String username,
                          @NotNull final String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    @NotNull
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @NotNull
    public String getDatabase() {
        return database;
    }

    @NotNull
    public String getUsername() {
        return username;
    }

    @NotNull
    public String getPassword() {
        return password;
    }
}
