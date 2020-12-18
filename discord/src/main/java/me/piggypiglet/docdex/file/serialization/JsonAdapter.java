package me.piggypiglet.docdex.file.serialization;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JsonAdapter implements FileAdapter {
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {}.getType();

    @NotNull
    @Override
    public Map<String, Object> fromString(@NotNull final String content) {
        return GSON.fromJson(content, MAP_TYPE);
    }
}
