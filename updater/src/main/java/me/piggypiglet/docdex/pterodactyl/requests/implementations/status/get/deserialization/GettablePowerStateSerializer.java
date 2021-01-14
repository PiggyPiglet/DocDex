package me.piggypiglet.docdex.pterodactyl.requests.implementations.status.get.deserialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import me.piggypiglet.docdex.pterodactyl.requests.implementations.status.get.GettablePowerState;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class GettablePowerStateSerializer implements JsonDeserializer<GettablePowerState> {
    @NotNull
    @Override
    public GettablePowerState deserialize(@NotNull final JsonElement json, @NotNull final Type typeOfT,
                                          @NotNull final JsonDeserializationContext context) {
        return GettablePowerState.valueOf(json.getAsJsonObject().get("attributes")
                .getAsJsonObject().get("current_state")
                .getAsString().toUpperCase());
    }
}
