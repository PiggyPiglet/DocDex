package me.piggypiglet.docdex.documentation.objects;

import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2021
// https://www.piggypiglet.me
// ------------------------------
public enum MongoDocumentedObjectFields {
    IDENTIFIER("identifier"),
    NAME("name"),
    FQN("fqn"),
    FULL_PARAMS("fullParams"),
    TYPE_PARAMS("typeParams"),
    FQN_TYPE_PARAMS("fqnTypeParams"),
    NAME_PARAMS("nameParams"),
    FQN_NAME_PARAMS("fqnNameParams");

    private final String name;

    MongoDocumentedObjectFields(@NotNull final String name) {
        this.name = name;
    }

    @NotNull
    public String getName() {
        return name;
    }
}
