package me.piggypiglet.docdex.file.adaptation;

import com.google.gson.reflect.TypeToken;
import com.google.inject.ImplementedBy;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@ImplementedBy(JsonAdapter.class)
public interface FileAdapter {
    Type MAP_TYPE = new TypeToken<Map<String, Object>>() {}.getType();

    @NotNull
    Map<String, Object> fromString(@NotNull final String content);

    @NotNull
    String toString(@NotNull final Map<String, Object> data);
}
