package me.piggypiglet.docdex.config.deserialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import net.dv8tion.jda.api.OnlineStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class StatusDeserializer implements JsonDeserializer<OnlineStatus> {
    @NotNull
    @Override
    public OnlineStatus deserialize(@NotNull final JsonElement json, @NotNull final Type typeOfT,
                                    @NotNull final JsonDeserializationContext context) {
        return OnlineStatus.valueOf(json.getAsString().toUpperCase());
    }
}
