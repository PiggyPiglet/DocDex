package me.piggypiglet.docdex.config;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class CommandRule {
    private final Set<String> allowed;
    private final Set<String> disallowed;
    private final String recommendation;

    public CommandRule(@NotNull final Set<String> allowed, @NotNull final Set<String> disallowed,
                       @NotNull final String recommendation) {
        this.allowed = allowed;
        this.disallowed = disallowed;
        this.recommendation = recommendation;
    }

    @NotNull
    public Set<String> getAllowed() {
        return allowed;
    }

    @NotNull
    public Set<String> getDisallowed() {
        return disallowed;
    }

    @NotNull
    public String getRecommendation() {
        return recommendation;
    }

    @Override
    public String toString() {
        return "CommandRule{" +
                "allowed=" + allowed +
                ", disallowed=" + disallowed +
                ", recommendation='" + recommendation + '\'' +
                '}';
    }
}
