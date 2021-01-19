package me.piggypiglet.docdex.db.server;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JavadocCategory {
    private final String name;
    private final String description;
    private final Set<String> javadocs;

    public JavadocCategory(@NotNull final String name, @NotNull final String description,
                           @NotNull final Set<String> javadocs) {
        this.name = name;
        this.description = description;
        this.javadocs = javadocs;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    @NotNull
    public Set<String> getJavadocs() {
        return javadocs;
    }
}
