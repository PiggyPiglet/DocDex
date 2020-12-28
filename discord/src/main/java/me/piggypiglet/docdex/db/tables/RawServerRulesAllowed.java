package me.piggypiglet.docdex.db.tables;

import me.piggypiglet.docdex.db.orm.annotations.Identifier;
import me.piggypiglet.docdex.db.orm.annotations.Table;
import me.piggypiglet.docdex.db.tables.framework.RawServerRuleId;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Table("servers_rules_allowed")
public final class RawServerRulesAllowed implements RawServerRuleId {
    private final String server;
    private final String command;
    @Identifier private final String allowed;

    public RawServerRulesAllowed(@NotNull final String server, @NotNull final String command,
                                 @NotNull final String allowed) {
        this.server = server;
        this.command = command;
        this.allowed = allowed;
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
    @Override
    public String getId() {
        return allowed;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RawServerRulesAllowed that = (RawServerRulesAllowed) o;
        return server.equals(that.server) && command.equals(that.command) && allowed.equals(that.allowed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, command, allowed);
    }

    @Override
    public String toString() {
        return "RawServerRulesAllowed{" +
                "id='" + server + '\'' +
                ", key='" + command + '\'' +
                ", allowed='" + allowed + '\'' +
                '}';
    }
}
