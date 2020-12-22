package me.piggypiglet.docdex.config;

import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MysqlConfig {
    private String host;
    private int port;
    private String username;
    private String password;
    private String database;
    private String tablePrefix;
    private int poolSize;

    @NotNull
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @NotNull
    public String getUsername() {
        return username;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    @NotNull
    public String getDatabase() {
        return database;
    }

    @NotNull
    public String getTablePrefix() {
        return tablePrefix;
    }

    public int getPoolSize() {
        return poolSize;
    }
}
