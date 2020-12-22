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
        externalPath = "config.json"
)
@Singleton
public final class Config {
    private String host;
    private int port;
    private MongoConfig database;
    private Set<Javadoc> javadocs;
    private int threads;

    @NotNull
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @NotNull
    public MongoConfig getDatabase() {
        return database;
    }

    @NotNull
    public Set<Javadoc> getJavadocs() {
        return javadocs;
    }

    public int getThreads() {
        return threads;
    }
}
