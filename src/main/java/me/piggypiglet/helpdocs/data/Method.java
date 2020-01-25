package me.piggypiglet.helpdocs.data;

import me.piggypiglet.framework.utils.SearchUtils;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class Method implements SearchUtils.Searchable {
    private final String name;
    private final String type;
    private final String description;

    public Method(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
