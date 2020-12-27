package me.piggypiglet.docdex.db.orm.structure.objects;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public enum SqlDataStructures {
    TEXT(767, String.class);

    private static final Map<Class<?>, SqlDataStructures> MAP = new HashMap<>();

    static {
        for (final SqlDataStructures dataStructure : values()) {
            for (final Class<?> type : dataStructure.getTypes()) {
                MAP.put(type, dataStructure);
            }
        }
    }

    private final int length;
    private final Set<Class<?>> types;

    SqlDataStructures(@NotNull final Class<?> @NotNull ... types) {
        this(-1, types);
    }

    SqlDataStructures(final int length, @NotNull final Class<?> @NotNull ... types) {
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

    @NotNull
    public static Optional<SqlDataStructures> fromType(@NotNull final Class<?> type) {
        return Optional.ofNullable(MAP.getOrDefault(type, null));
    }
}
