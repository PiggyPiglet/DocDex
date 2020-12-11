package me.piggypiglet.docdex.documentation.objects.util;

import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class PotentialObject {
    private final String fqn;
    private final DocumentedObject object;

    private PotentialObject(@Nullable final String fqn, @Nullable final DocumentedObject object) {
        this.fqn = fqn;
        this.object = object;
    }

    @NotNull
    public static PotentialObject of(@NotNull final Object object) {
        return object instanceof String ? new PotentialObject((String) object, null) : new PotentialObject(null, (DocumentedObject) object);
    }

    @Nullable
    public String getFqn() {
        return fqn;
    }

    @Nullable
    public DocumentedObject getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "PotentialObject{" +
                "fqn='" + fqn + '\'' +
                ", object=" + object +
                '}';
    }
}
