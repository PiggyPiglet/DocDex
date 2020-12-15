package me.piggypiglet.docdex.db.objects;

import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MongoDocumentedObject {
    private final String name;
    private final DocumentedObject object;

    public MongoDocumentedObject(@NotNull final String name, @NotNull final DocumentedObject object) {
        this.name = name;
        this.object = object;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public DocumentedObject getObject() {
        return object;
    }
}
