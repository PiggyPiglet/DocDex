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
@Table("servers_javadoc_categories_javadocs")
public final class RawServerJavadocCategoriesJavadocs implements RawObject {
    @Identifier @Length(ID_LENGTH)
    private final String server;
    private final String category;
    @Identifier
    private final String name;

    public RawServerJavadocCategoriesJavadocs(@NotNull final String server, @NotNull final String category,
                                              @NotNull final String name) {
        this.server = server;
        this.category = category;
        this.name = name;
    }

    @NotNull
    public String getServer() {
        return server;
    }

    @NotNull
    public String getCategory() {
        return category;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Override
    public boolean actualEquals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RawServerJavadocCategoriesJavadocs that = (RawServerJavadocCategoriesJavadocs) o;
        return server.equals(that.server) && name.equals(that.name) && category.equals(that.category);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RawServerJavadocCategoriesJavadocs that = (RawServerJavadocCategoriesJavadocs) o;
        return server.equals(that.server) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, name);
    }
}
