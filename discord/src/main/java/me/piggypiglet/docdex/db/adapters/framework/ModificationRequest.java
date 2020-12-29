package me.piggypiglet.docdex.db.adapters.framework;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ModificationRequest {
    private final Set<Object> modified;
    private final Set<Object> deleted;

    public ModificationRequest(@NotNull final Set<Object> modified, @NotNull final Set<Object> deleted) {
        this.modified = modified;
        this.deleted = deleted;
    }

    @NotNull
    public Set<Object> getModified() {
        return modified;
    }

    @NotNull
    public Set<Object> getDeleted() {
        return deleted;
    }
}
