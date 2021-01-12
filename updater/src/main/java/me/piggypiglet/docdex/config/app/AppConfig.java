package me.piggypiglet.docdex.config.app;

import me.piggypiglet.docdex.config.Javadoc;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class AppConfig {
    private final String host;
    private final int port;
    private final AppMongoConfig database;
    private final Set<Javadoc> javadocs;

    public AppConfig(@NotNull final String host, final int port,
                     @NotNull final AppMongoConfig database, @NotNull final Set<Javadoc> javadocs) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.javadocs = javadocs;
    }

    @NotNull
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @NotNull
    public AppMongoConfig getDatabase() {
        return database;
    }

    @NotNull
    public Set<Javadoc> getJavadocs() {
        return javadocs;
    }
}
