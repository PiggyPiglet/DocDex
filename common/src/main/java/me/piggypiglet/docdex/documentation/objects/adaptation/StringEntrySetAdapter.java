package me.piggypiglet.docdex.documentation.objects.adaptation;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class StringEntrySetAdapter implements JsonSerializer<Set<Map.Entry<String, String>>>, JsonDeserializer<Set<Map.Entry<String, String>>> {
    @NotNull
    @Override
    public JsonElement serialize(@NotNull final Set<Map.Entry<String, String>> src, @NotNull final Type typeOfSrc,
                                 @NotNull final JsonSerializationContext context) {
        return context.serialize(src);
    }

    @NotNull
    @Override
    public Set<Map.Entry<String, String>> deserialize(@NotNull final JsonElement json, @NotNull final Type typeOfT,
                                                      @NotNull final JsonDeserializationContext context) {
        return StreamSupport.stream(json.getAsJsonArray().spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .map(object -> Map.entry(object.get("key").getAsString(), object.get("value").getAsString()))
                .collect(Collectors.toSet());
    }
}
