package me.piggypiglet.docdex.db.server.creation;

import me.piggypiglet.docdex.db.dbo.framework.DatabaseObjectCreator;
import me.piggypiglet.docdex.db.server.JavadocCategory;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JavadocCategoryCreator implements DatabaseObjectCreator<JavadocCategory> {
    @Override
    public @NotNull JavadocCategory createInstance() {
        return new JavadocCategory("A bunch of javadocs.", new HashSet<>());
    }
}
