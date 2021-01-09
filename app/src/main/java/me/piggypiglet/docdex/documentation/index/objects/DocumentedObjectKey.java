package me.piggypiglet.docdex.documentation.index.objects;

import me.piggypiglet.docdex.documentation.utils.ParameterTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DocumentedObjectKey {
    private final String name;
    private final String fqn;
    private final Map<ParameterTypes, String> params;

    public DocumentedObjectKey(@NotNull final String name, @NotNull final String fqn,
                               @NotNull final Map<ParameterTypes, String> params) {
        this.name = name;
        this.fqn = fqn;
        this.params = params;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getFqn() {
        return fqn;
    }

    @NotNull
    public Map<ParameterTypes, String> getParams() {
        return params;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DocumentedObjectKey that = (DocumentedObjectKey) o;
        return name.equals(that.name) && fqn.equals(that.fqn) && params.equals(that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fqn, params);
    }

    @Override
    public String toString() {
        return "DocumentedObjectKey{" +
                "name='" + name + '\'' +
                ", fqn='" + fqn + '\'' +
                ", params=" + params +
                '}';
    }
}
