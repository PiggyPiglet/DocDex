package me.piggypiglet.docdex.db.server;

import me.piggypiglet.docdex.documentation.index.algorithm.Algorithm;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class Server {
    private final String id;
    private String prefix;
    private Algorithm algorithm;
    private String defaultJavadoc;
    private final Set<String> roles;
    private final Map<String, CommandRule> rules;
    private final Map<String, JavadocCategory> javadocCategories;

    public Server(@NotNull final String id, @NotNull final String prefix,
                  @NotNull final Algorithm algorithm, @NotNull final String defaultJavadoc,
                  @NotNull final Set<String> roles, @NotNull final Map<String, CommandRule> rules,
                  @NotNull final Map<String, JavadocCategory> javadocCategories) {
        this.id = id;
        this.prefix = prefix;
        this.algorithm = algorithm;
        this.defaultJavadoc = defaultJavadoc;
        this.roles = roles;
        this.rules = rules;
        this.javadocCategories = javadocCategories;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(@NotNull final String prefix) {
        this.prefix = prefix;
    }

    @NotNull
    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(@NotNull final Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    @NotNull
    public String getDefaultJavadoc() { return defaultJavadoc; }

    public void setDefaultJavadoc(@NotNull final String defaultJavadoc) { this.defaultJavadoc = defaultJavadoc; }

    @NotNull
    public Set<String> getRoles() {
        return roles;
    }

    @NotNull
    public Map<String, CommandRule> getRules() {
        return rules;
    }

    @NotNull
    public Map<String, JavadocCategory> getJavadocCategories() {
        return javadocCategories;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Server server = (Server) o;
        return id.equals(server.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Server{" +
                "id='" + id + '\'' +
                ", prefix='" + prefix + '\'' +
                ", defaultJavadoc='" + defaultJavadoc + '\'' +
                ", rules=" + rules +
                ", javadocCategories=" + javadocCategories +
                '}';
    }
}
