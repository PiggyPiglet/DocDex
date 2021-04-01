package me.piggypiglet.docdex.config.deserialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import me.piggypiglet.docdex.config.UpdateStrategyType;
import me.piggypiglet.docdex.config.strategies.UpdateStrategy;
import me.piggypiglet.docdex.config.strategies.direct.DirectZipStrategy;
import me.piggypiglet.docdex.config.strategies.maven.MavenLatestStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class UpdateStrategyDeserializer implements JsonDeserializer<UpdateStrategy> {
    @Nullable
    @Override
    public UpdateStrategy deserialize(@NotNull final JsonElement json, @NotNull final Type typeOfT,
                              @NotNull final JsonDeserializationContext context) {
        if (json.isJsonNull()) {
            return null;
        }

        UpdateStrategyType strategy;

        try {
            strategy = UpdateStrategyType.valueOf(json.getAsJsonObject().get("type").getAsString().toUpperCase());
        } catch (EnumConstantNotPresentException exception) {
            strategy = UpdateStrategyType.NONE;
        }

        switch (strategy) {
            case MAVEN_LATEST_RELEASE:
            case MAVEN_LATEST:
                return context.deserialize(json, MavenLatestStrategy.class);

            case DIRECT_ZIP:
                return context.deserialize(json, DirectZipStrategy.class);

            default:
                return null;
        }
    }
}
