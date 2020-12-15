package me.piggypiglet.docdex.documentation.index.data.storage;

import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public interface IndexStorage {
    void save(@NotNull final Javadoc javadoc, @NotNull final Map<String, DocumentedObject> objects);
}
