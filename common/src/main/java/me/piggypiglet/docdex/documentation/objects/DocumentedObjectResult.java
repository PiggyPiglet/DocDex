package me.piggypiglet.docdex.documentation.objects;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// ------------------------------
// Copyright (c) PiggyPiglet 2021
// https://www.piggypiglet.me
// ------------------------------
public final class DocumentedObjectResult {
    private final String name;
    private final DocumentedObject object;

    public DocumentedObjectResult(@NotNull final String name, @NotNull final DocumentedObject object) {
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DocumentedObjectResult that = (DocumentedObjectResult) o;
        return name.equals(that.name) && object.equals(that.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, object);
    }

    @Override
    public String toString() {
        return "DocumentedObjectResult{" +
                "name='" + name + '\'' +
                ", object=" + object +
                '}';
    }
}
