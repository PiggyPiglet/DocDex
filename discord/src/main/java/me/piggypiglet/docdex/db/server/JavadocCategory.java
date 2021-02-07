package me.piggypiglet.docdex.db.server;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JavadocCategory {
    private String description;
    private final Set<String> javadocs;

    public JavadocCategory(@NotNull final String description, @NotNull final Set<String> javadocs) {
        this.description = description;
        this.javadocs = javadocs;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NotNull final String description) {
        this.description = description;
    }

    @NotNull
    public Set<String> getJavadocs() {
        return javadocs;
    }

    @Override
    public String toString() {
        return "JavadocCategory{" +
                "description='" + description + '\'' +
                ", javadocs=" + javadocs +
                '}';
    }
}
