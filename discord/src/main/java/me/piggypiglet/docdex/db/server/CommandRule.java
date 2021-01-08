package me.piggypiglet.docdex.db.server;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class CommandRule {
    private final Set<String> allowed;
    private final Set<String> disallowed;
    private String recommendation;

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

    public void setRecommendation(@NotNull final String value) {
        recommendation = value;
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
