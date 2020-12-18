package me.piggypiglet.docdex.config;

import com.google.gson.annotations.JsonAdapter;
import com.google.inject.Singleton;
import me.piggypiglet.docdex.config.deserialization.UrlDeserializer;
import me.piggypiglet.docdex.file.annotations.File;
import org.jetbrains.annotations.NotNull;

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
    private String token;
    private String prefix;
    @JsonAdapter(UrlDeserializer.class) private String url;

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
}
