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
    private final String prefix;
    private final String algorithm;
    private final String defaultJavadoc;

    public RawServer(@NotNull final String id, @NotNull final String prefix,
                     @NotNull final String algorithm, @NotNull final String defaultJavadoc) {
        this.id = id;
        this.prefix = prefix;
        this.algorithm = algorithm;
        this.defaultJavadoc = defaultJavadoc;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getPrefix() {
        return prefix;
    }

    @NotNull
    public String getAlgorithm() {
        return algorithm;
    }

    @NotNull
    public String getDefaultJavadoc() { return defaultJavadoc; }

    @Override
    public boolean actualEquals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RawServer rawServer = (RawServer) o;
        return id.equals(rawServer.id) && prefix.equals(rawServer.prefix) && algorithm.equals(rawServer.algorithm) && defaultJavadoc.equals(rawServer.defaultJavadoc);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RawServer rawServer = (RawServer) o;
        return id.equals(rawServer.id);
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
                ", algorithm='" + algorithm + '\'' +
                ", defaultJavadoc='" + defaultJavadoc + '\'' +
                '}';
    }
}
