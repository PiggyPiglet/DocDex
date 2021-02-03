package me.piggypiglet.docdex.config;

import com.google.gson.annotations.JsonAdapter;
import com.google.inject.Singleton;
import me.piggypiglet.docdex.config.deserialization.UrlDeserializer;
import me.piggypiglet.docdex.db.server.JavadocCategory;
import me.piggypiglet.docdex.file.annotations.File;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@File(
        internalPath = "/config.json",
        externalPath = "./config.json",
        stopOnFirstCreate = true
)
@Singleton
public final class Config {
    private String token;
    private String prefix;
    @JsonAdapter(UrlDeserializer.class) private String url;
    private String defaultJavadoc;
    private Map<String, JavadocCategory> defaultCategories;
    private Presence presence;
    private MysqlConfig mysql;

    @NotNull
    public String getToken() {
        return token;
    }

    @NotNull
    public String getPrefix() {
        return prefix;
    }

    @NotNull
    public String getUrl() {
        return url;
    }

    @NotNull
    public String getDefaultJavadoc() {
        return defaultJavadoc;
    }

    @NotNull
    public Map<String, JavadocCategory> getDefaultCategories() {
        return defaultCategories;
    }

    @NotNull
    public Presence getPresence() {
        return presence;
    }

    @NotNull
    public MysqlConfig getMysql() {
        return mysql;
    }
}
