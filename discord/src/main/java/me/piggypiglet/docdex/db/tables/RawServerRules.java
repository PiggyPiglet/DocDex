package me.piggypiglet.docdex.db.tables;

import me.piggypiglet.docdex.db.orm.annotations.Identifier;
import me.piggypiglet.docdex.db.orm.annotations.Table;
import me.piggypiglet.docdex.db.tables.framework.RawServerRule;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Table("servers_rules")
public final class RawServerRules implements RawServerRule {
    private final String server;
    @Identifier private final String command;
    private String recommendation;

    public RawServerRules(@NotNull final String server, @NotNull final String command,
                          @NotNull final String recommendation) {
        this.server = server;
        this.command = command;
        this.recommendation = recommendation;
    }

    @NotNull
    @Override
    public String getServer() {
        return server;
    }

    @NotNull
    @Override
    public String getCommand() {
        return command;
    }

    @NotNull
    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(@NotNull final String value) {
        recommendation = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RawServerRules that = (RawServerRules) o;
        return server.equals(that.server) && command.equals(that.command) && recommendation.equals(that.recommendation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, command, recommendation);
    }

    @Override
    public String toString() {
        return "RawServerRules{" +
                "id='" + server + '\'' +
                ", key='" + command + '\'' +
                ", recommendation='" + recommendation + '\'' +
                '}';
    }
}
