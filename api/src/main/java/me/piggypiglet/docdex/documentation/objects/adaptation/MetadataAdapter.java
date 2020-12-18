package me.piggypiglet.docdex.documentation.objects.adaptation;

import com.google.gson.*;
import me.piggypiglet.docdex.documentation.objects.detail.method.MethodMetadata;
import me.piggypiglet.docdex.documentation.objects.type.TypeMetadata;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MetadataAdapter implements JsonSerializer<Object>, JsonDeserializer<Object> {
    @NotNull
    @Override
    public JsonElement serialize(@NotNull final Object src, @NotNull final Type typeOfSrc,
                                 @NotNull final JsonSerializationContext context) {
        return context.serialize(src);
    }

    @NotNull
    @Override
    public Object deserialize(@NotNull final JsonElement json, @NotNull final Type typeOfT,
                              @NotNull final JsonDeserializationContext context) {
        return context.deserialize(json, json.getAsJsonObject().has("extensions") ? TypeMetadata.class : MethodMetadata.class);
    }
}
