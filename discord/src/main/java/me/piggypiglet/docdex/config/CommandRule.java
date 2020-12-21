package me.piggypiglet.docdex.config;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class CommandRule {
    private Set<String> allowed;
    private Set<String> disallowed;
    private String recommendation;

    @NotNull
    public Set<String> getAllowed() {
        return allowed == null ? Collections.emptySet() : allowed;
    }

    @NotNull
    public Set<String> getDisallowed() {
        return disallowed == null ? Collections.emptySet() : disallowed;
    }

    @NotNull
    public String getRecommendation() {
        return recommendation;
    }
}
