package me.piggypiglet.docdex.documentation.routes.serialization.implementations;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import me.piggypiglet.docdex.documentation.index.data.utils.DataUtils;
import me.piggypiglet.docdex.documentation.objects.util.PotentialObject;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Optional;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class PotentialObjectSerializer implements JsonSerializer<PotentialObject> {
    @NotNull
    @Override
    public JsonElement serialize(@NotNull final PotentialObject src, @NotNull final Type typeOfSrc,
                                 @NotNull final JsonSerializationContext context) {
        return context.serialize(Optional.ofNullable(src.getObject())
                .map(DataUtils::getFqn)
                .orElse(src.getFqn()));
    }
}
