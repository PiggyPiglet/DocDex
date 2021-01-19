package me.piggypiglet.docdex.db.tables.rules;

import me.piggypiglet.docdex.db.orm.annotations.Identifier;
import me.piggypiglet.docdex.db.orm.annotations.Length;
import me.piggypiglet.docdex.db.orm.annotations.Table;
import me.piggypiglet.docdex.db.tables.framework.RawObject;
import me.piggypiglet.docdex.db.tables.framework.RawServerRuleId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Table("servers_rules_disallowed")
public final class RawServerRulesDisallowed implements RawServerRuleId, RawObject {
    @Length(ID_LENGTH)
    private final String server;
    private final String command;
    @Identifier @Length(ID_LENGTH)
    private final String id;

    public RawServerRulesDisallowed(@NotNull final String server, @NotNull final String command,
                                    @NotNull final String id) {
        this.server = server;
        this.command = command;
        this.id = id;
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
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RawServerRulesDisallowed that = (RawServerRulesDisallowed) o;
        return server.equals(that.server) && command.equals(that.command) && id.equals(that.id);
    }

    @Override
    public boolean actualEquals(final @Nullable Object o) {
        return equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, command, id);
    }

    @Override
    public String toString() {
        return "RawServerRulesDisallowed{" +
                "id='" + server + '\'' +
                ", key='" + command + '\'' +
                ", disallowed='" + id + '\'' +
                '}';
    }
}
