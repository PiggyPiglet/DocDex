package me.piggypiglet.docdex.config;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class Javadoc {
    private Set<String> names;
    private String link;
    private String actualLink;

    @NotNull
    public Set<String> getNames() {
        return names;
    }

    @NotNull
    public String getLink() {
        return link;
    }

    @NotNull
    public String getActualLink() {
        return actualLink;
    }
}
