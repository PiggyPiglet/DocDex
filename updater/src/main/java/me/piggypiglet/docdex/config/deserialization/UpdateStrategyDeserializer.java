package me.piggypiglet.docdex.config.deserialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import me.piggypiglet.docdex.config.UpdateStrategy;
import me.piggypiglet.docdex.config.strategies.maven.MavenLatestStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class UpdateStrategyDeserializer implements JsonDeserializer<Object> {
    @Nullable
    @Override
    public Object deserialize(@NotNull final JsonElement json, @NotNull final Type typeOfT,
                              @NotNull final JsonDeserializationContext context) {
        if (json.isJsonNull()) {
            return null;
        }

        UpdateStrategy strategy;

        try {
            strategy = UpdateStrategy.valueOf(json.getAsJsonObject().get("type").getAsString().toUpperCase());
        } catch (EnumConstantNotPresentException exception) {
            strategy = UpdateStrategy.NONE;
        }

        switch (strategy) {
            case MAVEN_LATEST_RELEASE:
            case MAVEN_LATEST_SNAPSHOT:
                return context.deserialize(json, MavenLatestStrategy.class);

            default:
                return null;
        }
    }
}
