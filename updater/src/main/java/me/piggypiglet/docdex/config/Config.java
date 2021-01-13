package me.piggypiglet.docdex.config;

import com.google.gson.annotations.JsonAdapter;
import com.google.inject.Singleton;
import me.piggypiglet.docdex.config.deserialization.TimeDeserializer;
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
    private String pterodactylLink;
    private String apiToken;
    private String appConfigPath;
    @JsonAdapter(TimeDeserializer.class) private long updateFrequency;
    private Set<UpdaterJavadoc> javadocs;

    @NotNull
    public String getPterodactylLink() {
        return pterodactylLink;
    }

    @NotNull
    public String getApiToken() {
        return apiToken;
    }

    @NotNull
    public String getAppConfigPath() {
        return appConfigPath;
    }

    public long getUpdateFrequency() {
        return updateFrequency;
    }

    @NotNull
    public Set<UpdaterJavadoc> getJavadocs() {
        return javadocs;
    }
}
