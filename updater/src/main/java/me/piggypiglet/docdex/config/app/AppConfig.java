package me.piggypiglet.docdex.config.app;

import com.google.inject.Singleton;
import me.piggypiglet.docdex.config.Javadoc;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class AppConfig {
    private String host;
    private int port;
    private AppMongoConfig database;
    private Set<Javadoc> javadocs;

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

    @Override
    public String toString() {
        return "AppConfig{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", database=" + database +
                ", javadocs=" + javadocs +
                '}';
    }
}
