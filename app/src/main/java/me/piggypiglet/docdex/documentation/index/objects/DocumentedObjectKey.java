package me.piggypiglet.docdex.documentation.index.objects;

import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DocumentedObjectKey {
    private final String name;
    private final String fqn;

    public DocumentedObjectKey(@NotNull final String name, @NotNull final String fqn) {
        this.name = name;
        this.fqn = fqn;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getFqn() {
        return fqn;
    }
}
