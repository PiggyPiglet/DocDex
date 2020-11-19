package me.piggypiglet.docdex.documentation.objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DocumentedObject {
    private final DocumentedTypes type;
    private final String name;
    private final String description;
    private final Set<String> annotations;
    private final boolean deprecated;
    private final String deprecationMessage;
    private final Set<String> modifiers;
    private final Object metadata;

    public DocumentedObject(@NotNull final DocumentedTypes type, @NotNull final String name,
                            @Nullable final String description, @NotNull final Set<String> annotations,
                            final boolean deprecated, @Nullable final String deprecationMessage,
                            @NotNull final Set<String> modifiers, @NotNull final Object metadata) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.annotations = annotations;
        this.deprecated = deprecated;
        this.deprecationMessage = deprecationMessage;
        this.modifiers = modifiers;
        this.metadata = metadata;
    }

    @NotNull
    public DocumentedTypes getType() {
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

    @NotNull
    public Set<String> getAnnotations() {
        return annotations;
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

    @NotNull
    public Object getMetadata() {
        return metadata;
    }
}
