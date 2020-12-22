package me.piggypiglet.docdex.documentation.objects.adaptation.parameters;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ParameterDescriptionsAdapter implements JsonSerializer<Map<String, String>>, JsonDeserializer<Map<String, String>> {
    @NotNull
    @Override
    public JsonElement serialize(@NotNull final Map<String, String> src, @NotNull final Type typeOfSrc,
                                 @NotNull final JsonSerializationContext context) {
        return context.serialize(src.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().replace("$", "!"), Map.Entry::getValue)));
    }

    @NotNull
    @Override
    public Map<String, String> deserialize(@NotNull final JsonElement json, @NotNull final Type typeOfT,
                                           @NotNull final JsonDeserializationContext context) {
        return ((Map<String, String>) context.deserialize(json, typeOfT)).entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().replace("!", "$"), Map.Entry::getValue));
    }
}
