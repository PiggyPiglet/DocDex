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
@Table("servers_rules_disallowed")
public final class RawServerRulesDisallowed implements RawServerRuleId {
    private final String server;
    private final String command;
    @Identifier private final String disallowed;

    public RawServerRulesDisallowed(@NotNull final String server, @NotNull final String command,
                                    @NotNull final String disallowed) {
        this.server = server;
        this.command = command;
        this.disallowed = disallowed;
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
        return disallowed;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RawServerRulesDisallowed that = (RawServerRulesDisallowed) o;
        return server.equals(that.server) && command.equals(that.command) && disallowed.equals(that.disallowed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, command, disallowed);
    }

    @Override
    public String toString() {
        return "RawServerRulesDisallowed{" +
                "id='" + server + '\'' +
                ", key='" + command + '\'' +
                ", disallowed='" + disallowed + '\'' +
                '}';
    }
}
