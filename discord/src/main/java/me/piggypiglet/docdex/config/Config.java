package me.piggypiglet.docdex.config;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.inject.Singleton;
import me.piggypiglet.docdex.config.deserialization.UrlDeserializer;
import me.piggypiglet.docdex.file.annotations.File;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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
    private String defaultJavadoc;
    private Presence presence;
    @SerializedName("public") private boolean isPublic;
    private MysqlConfig mysql;
    private Map<String, CommandRule> commands;

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
    public Presence getPresence() {
        return presence;
    }

    public boolean isPublic() {
        return isPublic;
    }

    @NotNull
    public MysqlConfig getMysql() {
        return mysql;
    }

    @NotNull
    public Map<String, CommandRule> getCommands() {
        return commands;
    }
}
