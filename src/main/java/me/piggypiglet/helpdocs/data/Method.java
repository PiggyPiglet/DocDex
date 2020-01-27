package me.piggypiglet.helpdocs.data;

import me.piggypiglet.framework.utils.SearchUtils;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class Method implements SearchUtils.Searchable {
    private final String name;
    private final Metadata metadata;

    public Method(String name, Metadata metadata) {
        this.name = name;
        this.metadata = metadata;
    }

    @Override
    public String getName() {
        return name;
    }

    public Metadata getMetadata() {
        return metadata;
    }
}
