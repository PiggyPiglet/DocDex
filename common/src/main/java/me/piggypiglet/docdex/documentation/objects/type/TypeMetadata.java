package me.piggypiglet.docdex.documentation.objects.type;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TypeMetadata that = (TypeMetadata) o;
        return extensions.equals(that.extensions) && implementations.equals(that.implementations) && allImplementations.equals(that.allImplementations) && superInterfaces.equals(that.superInterfaces) && subInterfaces.equals(that.subInterfaces) && subClasses.equals(that.subClasses) && implementingClasses.equals(that.implementingClasses) && methods.equals(that.methods) && fields.equals(that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(extensions, implementations, allImplementations, superInterfaces, subInterfaces, subClasses, implementingClasses, methods, fields);
    }

    @Override
    public String toString() {
        return "TypeMetadata{" +
                "extensions=" + extensions +
                ", implementations=" + implementations +
                ", allImplementations=" + allImplementations +
                ", superInterfaces=" + superInterfaces +
                ", subInterfaces=" + subInterfaces +
                ", subClasses=" + subClasses +
                ", implementingClasses=" + implementingClasses +
                ", methods=" + methods +
                ", fields=" + fields +
                '}';
    }
}
