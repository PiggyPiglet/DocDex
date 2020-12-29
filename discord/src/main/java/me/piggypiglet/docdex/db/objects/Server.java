package me.piggypiglet.docdex.db.objects;

import me.piggypiglet.docdex.config.CommandRule;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class Server {
    private final String id;
    private String prefix;
    private final Map<String, CommandRule> rules;

    public Server(@NotNull final String id, @NotNull final String prefix,
                  @NotNull final Map<String, CommandRule> rules) {
        this.id = id;
        this.prefix = prefix;
        this.rules = rules;
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
    public Map<String, CommandRule> getRules() {
        return rules;
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
                ", rules=" + rules +
                '}';
    }
}
