package me.piggypiglet.docdex.documentation.objects;

import com.google.gson.annotations.JsonAdapter;
import me.piggypiglet.docdex.documentation.objects.adaptation.MetadataAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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
    @JsonAdapter(MetadataAdapter.class) private final Object metadata;

    private DocumentedObject(@NotNull final DocumentedTypes type, @NotNull final String name,
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

    @Override
    public String toString() {
        return "DocumentedObject{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", annotations=" + annotations +
                ", deprecated=" + deprecated +
                ", deprecationMessage='" + deprecationMessage + '\'' +
                ", modifiers=" + modifiers +
                ", metadata=" + metadata +
                '}';
    }

    @SuppressWarnings("UnusedReturnValue")
    public static abstract class Builder<T extends Builder<T>> {
        @SuppressWarnings("unchecked")
        private final T instance = (T) this;

        private DocumentedTypes type;
        private String name;
        private String description = null;
        private final Set<String> annotations = new HashSet<>();
        private boolean deprecated = false;
        private String deprecationMessage = null;
        private final Set<String> modifiers = new HashSet<>();

        protected Builder() {}

        @NotNull
        public T type(@NotNull final DocumentedTypes value) {
            type = value;
            return instance;
        }

        @NotNull
        public T name(@NotNull final String value) {
            name = value;
            return instance;
        }

        @NotNull
        public T description(@NotNull final String value) {
            description = value;
            return instance;
        }

        @NotNull
        public T annotations(@NotNull final String @NotNull ... values) {
            Collections.addAll(annotations, values);
            return instance;
        }

        @NotNull
        public T annotations(@NotNull final Collection<String> values) {
            annotations.addAll(values);
            return instance;
        }

        @NotNull
        public T deprecated(final boolean value) {
            deprecated = value;
            return instance;
        }

        @NotNull
        public T deprecationMessage(@NotNull final String value) {
            deprecationMessage = value;
            return instance;
        }

        @NotNull
        public T modifiers(@NotNull final String @NotNull ... values) {
            Collections.addAll(modifiers, values);
            return instance;
        }

        @NotNull
        public T modifiers(@NotNull final Collection<String> values) {
            modifiers.addAll(values);
            return instance;
        }

        public abstract DocumentedObject build();

        @NotNull
        protected final DocumentedObject build(@NotNull final Object metadata) {
            return new DocumentedObject(type, name, description, annotations, deprecated, deprecationMessage,
                    modifiers, metadata);
        }
    }
}
