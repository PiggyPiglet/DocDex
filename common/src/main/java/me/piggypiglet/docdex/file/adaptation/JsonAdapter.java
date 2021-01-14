package me.piggypiglet.docdex.file.adaptation;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JsonAdapter implements FileAdapter {
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    @NotNull
    @Override
    public Map<String, Object> fromString(@NotNull final String content) {
        return GSON.fromJson(content, MAP_TYPE);
    }

    @NotNull
    @Override
    public String toString(final @NotNull Map<String, Object> data) {
        return GSON.toJson(data, MAP_TYPE);
    }
}
