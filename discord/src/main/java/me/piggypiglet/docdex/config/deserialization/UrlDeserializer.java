package me.piggypiglet.docdex.config.deserialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class UrlDeserializer implements JsonDeserializer<String> {
    @NotNull
    @Override
    public String deserialize(@NotNull final JsonElement json, @NotNull final Type typeOfT,
                              @NotNull final JsonDeserializationContext context) {
        String url = json.getAsString();
        url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        url = url.endsWith("index") ? url : url + "/index";
        return url;
    }
}
