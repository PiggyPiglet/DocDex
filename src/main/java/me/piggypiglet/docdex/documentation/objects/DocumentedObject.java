package me.piggypiglet.docdex.documentation.objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DocumentedObject {
    private final Types type;
    private final String name;
    private final String description;
    private final boolean deprecated;
    private final String deprecationMessage;
    private final Set<String> modifiers;

    public DocumentedObject(@NotNull final Types type, @NotNull final String name,
                            @NotNull final String description, final boolean deprecated,
                            @NotNull final String deprecationMessage, @NotNull final Set<String> modifiers) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.deprecated = deprecated;
        this.deprecationMessage = deprecationMessage;
        this.modifiers = modifiers;
    }

    @NotNull
    public Types getType() {
        return type;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    @Nullable
    public String getDeprecationMessage() {
        return deprecationMessage;
    }

    @NotNull
    public Set<String> getModifiers() {
        return modifiers;
    }
}
