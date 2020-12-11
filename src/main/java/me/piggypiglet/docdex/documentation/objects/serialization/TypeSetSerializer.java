package me.piggypiglet.docdex.documentation.objects.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import me.piggypiglet.docdex.documentation.objects.type.TypeMetadata;
import me.piggypiglet.docdex.documentation.objects.util.PotentialObject;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TypeSetSerializer implements JsonSerializer<Set<PotentialObject>> {
    @NotNull
    @Override
    public JsonElement serialize(@NotNull final Set<PotentialObject> src, @NotNull final Type typeOfSrc,
                                 @NotNull final JsonSerializationContext context) {
        return context.serialize(src.stream()
                .map(type -> Optional.ofNullable(type.getObject())
                        .map(obj -> ((TypeMetadata) obj.getMetadata()).getPackage() + '.' + obj.getName())
                        .orElse(type.getFqn()))
                .collect(Collectors.toSet()));
    }
}
