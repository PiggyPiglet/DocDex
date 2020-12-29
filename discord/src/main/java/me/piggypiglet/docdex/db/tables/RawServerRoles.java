package me.piggypiglet.docdex.db.tables;

import me.piggypiglet.docdex.db.orm.annotations.Identifier;
import me.piggypiglet.docdex.db.orm.annotations.Table;
import me.piggypiglet.docdex.db.tables.framework.RawObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Table("server_roles")
public final class RawServerRoles implements RawObject {
    @Identifier private final String server;
    private final String id;

    public RawServerRoles(@NotNull final String server, @NotNull final String id) {
        this.server = server;
        this.id = id;
    }

    @NotNull
    public String getServer() {
        return server;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RawServerRoles that = (RawServerRoles) o;
        return server.equals(that.server) && id.equals(that.id);
    }

    @Override
    public boolean actualEquals(final @Nullable Object o) {
        return equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, id);
    }

    @Override
    public String toString() {
        return "RawServerRoles{" +
                "server='" + server + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
