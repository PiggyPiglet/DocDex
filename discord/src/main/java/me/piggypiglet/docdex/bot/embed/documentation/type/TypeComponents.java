package me.piggypiglet.docdex.bot.embed.documentation.type;

import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public enum TypeComponents {
    EXTENSIONS("Extensions:"),
    IMPLEMENTATIONS("Implementations:"),
    ALL_IMPLEMENTATIONS("All Implementations:"),
    SUPER_INTERFACES("Super Interfaces:"),
    SUB_INTERFACES("Sub Interfaces:"),
    SUB_CLASSES("Sub Classes:"),
    IMPLEMENTING_CLASSES("Implementing Classes:"),
    METHODS("Methods:"),
    FIELDS("Fields:");

    private final String formattedName;

    TypeComponents(@NotNull final String formattedName) {
        this.formattedName = formattedName;
    }

    @NotNull
    public String getFormattedName() {
        return formattedName;
    }
}
