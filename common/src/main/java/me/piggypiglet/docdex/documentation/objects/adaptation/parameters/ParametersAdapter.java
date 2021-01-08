package me.piggypiglet.docdex.documentation.objects.adaptation.parameters;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ParametersAdapter implements JsonSerializer<Set<String>>, JsonDeserializer<Set<String>> {
    @NotNull
    @Override
    public JsonElement serialize(@NotNull final Set<String> src, @NotNull final Type typeOfSrc,
                                 @NotNull final JsonSerializationContext context) {
        return context.serialize(src.stream()
                .map(string -> string.replace("$", "!"))
                .collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public Set<String> deserialize(@NotNull final JsonElement json, @NotNull final Type typeOfT,
                                   @NotNull final JsonDeserializationContext context) {
        return ((Set<String>) context.deserialize(json, typeOfT)).stream()
                .map(string -> string.replace("!", "$"))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
