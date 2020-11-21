package me.piggypiglet.docdex.documentation.objects.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.type.TypeMetadata;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TypeSetSerializer implements JsonSerializer<Set<DocumentedObject>> {
    @NotNull
    @Override
    public JsonElement serialize(@NotNull final Set<DocumentedObject> src, @NotNull final Type typeOfSrc,
                                 @NotNull final JsonSerializationContext context) {
        return context.serialize(src.stream()
                .map(type -> ((TypeMetadata) type.getMetadata()).getPackage() + '.' + type.getName())
                .collect(Collectors.toSet()));
    }
}
