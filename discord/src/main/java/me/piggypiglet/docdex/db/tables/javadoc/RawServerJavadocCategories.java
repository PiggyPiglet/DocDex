package me.piggypiglet.docdex.db.tables.javadoc;

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
@Table("servers_javadoc_categories")
public final class RawServerJavadocCategories implements RawObject {
    @Identifier @Length(ID_LENGTH)
    private final String server;
    @Identifier
    private final String name;
    private final String description;

    public RawServerJavadocCategories(@NotNull final String server, @NotNull final String name,
                                      @NotNull final String description) {
        this.server = server;
        this.name = name;
        this.description = description;
    }

    @NotNull
    public String getServer() {
        return server;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    @Override
    public boolean actualEquals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RawServerJavadocCategories that = (RawServerJavadocCategories) o;
        return server.equals(that.server) && name.equals(that.name) && description.equals(that.description);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RawServerJavadocCategories that = (RawServerJavadocCategories) o;
        return server.equals(that.server) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, name);
    }
}
