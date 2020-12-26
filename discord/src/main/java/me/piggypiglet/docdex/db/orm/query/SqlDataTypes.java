package me.piggypiglet.docdex.db.orm.query;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public enum SqlDataTypes {
    TEXT(767, String.class),
    BIGINT(Long.class, long.class);

    private final int length;
    private final Set<Class<?>> types;

    SqlDataTypes(@NotNull final Class<?> @NotNull ... types) {
        this(-1, types);
    }

    SqlDataTypes(final int length, @NotNull final Class<?> @NotNull ... types) {
        this.length = length;
        this.types = Set.of(types);
    }

    public int getLength() {
        return length;
    }

    @NotNull
    public Set<Class<?>> getTypes() {
        return types;
    }

    @Override
    public String toString() {
        return name() + (length != -1 ? "(" + length + ")" : "");
    }

    @NotNull
    public static Optional<SqlDataTypes> from(@NotNull final Class<?> type) {
        return Arrays.stream(values())
                .filter(value -> value.getTypes().contains(type))
                .findAny();
    }
}
