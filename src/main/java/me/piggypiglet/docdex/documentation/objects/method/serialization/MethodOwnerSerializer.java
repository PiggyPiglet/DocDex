package me.piggypiglet.docdex.documentation.objects.method.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.type.TypeMetadata;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MethodOwnerSerializer implements JsonSerializer<DocumentedObject> {
    @NotNull
    @Override
    public JsonElement serialize(@NotNull final DocumentedObject src, @NotNull final Type typeOfSrc,
                                 @NotNull final JsonSerializationContext context) {
        return context.serialize(((TypeMetadata) src.getMetadata()).getPackage() + '.' + src.getName());
    }
}
