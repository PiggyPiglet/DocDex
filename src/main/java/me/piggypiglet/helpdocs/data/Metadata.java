package me.piggypiglet.helpdocs.data;

import me.piggypiglet.framework.utils.SearchUtils;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class Metadata implements SearchUtils.Searchable {
    private final String name;
    private final String type;
    private final String[] modifiers;
    private final boolean deprecated;
    private final String description;

    public Metadata(String name, String type, String[] modifiers, boolean deprecated, String description) {
        this.name = name;
        this.type = type;
        this.modifiers = modifiers;
        this.deprecated = deprecated;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String[] getModifiers() {
        return modifiers;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public String getDescription() {
        return description;
    }
}