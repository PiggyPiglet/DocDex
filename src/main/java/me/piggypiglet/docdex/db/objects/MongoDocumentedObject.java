package me.piggypiglet.docdex.db.objects;

import me.piggypiglet.docdex.documentation.index.data.utils.DataUtils;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MongoDocumentedObject {
    private final String name;
    private final String fqn;
    private final DocumentedObject object;

    public MongoDocumentedObject(@NotNull final String name, @NotNull final String fqn,
                                 @NotNull final DocumentedObject object) {
        this.name = name;
        this.fqn = fqn;
        this.object = object;
    }

    @NotNull
    public static MongoDocumentedObject from(@NotNull final DocumentedObject object) {
        return new MongoDocumentedObject(
                object.getName(),
                DataUtils.getFqn(object),
                object
        );
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
    public DocumentedObject getObject() {
        return object;
    }
}
