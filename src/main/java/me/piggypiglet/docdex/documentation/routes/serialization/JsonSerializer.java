package me.piggypiglet.docdex.documentation.routes.serialization;

import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public interface JsonSerializer {
    boolean shouldSerialize(@NotNull final DocumentedObject object);

    void serialize(@NotNull final DocumentedObject object, @NotNull final Map<String, Object> map);
}
