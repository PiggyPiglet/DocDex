package me.piggypiglet.docdex.db.server.commands.util;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public enum ModificationOptions {
    ADD("ed"),
    REMOVE("d");

    public static final Map<String, ModificationOptions> MAP = Map.of(
            "add", ModificationOptions.ADD,
            "remove", ModificationOptions.REMOVE
    );

    private final String extension;

    ModificationOptions(@NotNull final String extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        return name().toLowerCase() + extension;
    }
}
