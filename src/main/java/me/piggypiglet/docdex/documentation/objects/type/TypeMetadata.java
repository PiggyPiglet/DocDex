package me.piggypiglet.docdex.documentation.objects.type;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TypeMetadata {
    private final Set<String> extensions;
    private final Set<String> implementations;
    private final Set<String> allImplementations;
    private final Set<String> superInterfaces;
    private final Set<String> subInterfaces;
    private final Set<String> subClasses;
    private final Set<String> implementingClasses;
    private final Set<String> methods = new HashSet<>();
    private final Set<String> fields = new HashSet<>();

    public TypeMetadata(@NotNull final Set<String> extensions, @NotNull final Set<String> implementations,
                        @NotNull final Set<String> allImplementations, @NotNull final Set<String> superInterfaces,
                        @NotNull final Set<String> subInterfaces, @NotNull final Set<String> subClasses,
                        @NotNull final Set<String> implementingClasses) {
        this.extensions = extensions;
        this.implementations = implementations;
        this.allImplementations = allImplementations;
        this.superInterfaces = superInterfaces;
        this.subInterfaces = subInterfaces;
        this.subClasses = subClasses;
        this.implementingClasses = implementingClasses;
    }

    @NotNull
    public Set<String> getExtensions() {
        return extensions;
    }

    @NotNull
    public Set<String> getImplementations() {
        return implementations;
    }

    @NotNull
    public Set<String> getAllImplementations() {
        return allImplementations;
    }

    @NotNull
    public Set<String> getSuperInterfaces() {
        return superInterfaces;
    }

    @NotNull
    public Set<String> getSubInterfaces() {
        return subInterfaces;
    }

    @NotNull
    public Set<String> getSubClasses() {
        return subClasses;
    }

    @NotNull
    public Set<String> getImplementingClasses() {
        return implementingClasses;
    }

    @NotNull
    public Set<String> getMethods() {
        return methods;
    }

    @NotNull
    public Set<String> getFields() {
        return fields;
    }
}
