package me.piggypiglet.docdex.db.tables;

import me.piggypiglet.docdex.db.orm.annotations.Identifier;
import me.piggypiglet.docdex.db.orm.annotations.Length;
import me.piggypiglet.docdex.db.orm.annotations.Table;
import me.piggypiglet.docdex.db.tables.framework.RawObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Table("servers")
public final class RawServer implements RawObject {
    @Identifier @Length(ID_LENGTH)
    private final String id;
    private String prefix;

    public RawServer(@NotNull final String id, @NotNull final String prefix) {
        this.id = id;
        this.prefix = prefix;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getPrefix() {
        return prefix;
    }

    public boolean setPrefix(@NotNull final String value) {
        final boolean result = !value.equals(prefix);
        prefix = value;
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RawServer rawServer = (RawServer) o;
        return id.equals(rawServer.id);
    }

    @Override
    public boolean actualEquals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RawServer rawServer = (RawServer) o;
        return id.equals(rawServer.id) && prefix.equals(rawServer.prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RawServer{" +
                "id='" + id + '\'' +
                ", prefix='" + prefix + '\'' +
                '}';
    }
}
