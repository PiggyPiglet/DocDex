package me.piggypiglet.docdex.documentation.index.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class StreamUtils {
    private StreamUtils() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    public static <T> Predicate<T> distinctByKey(@NotNull final Function<? super T, ?> keyExtractor) {
        final Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
