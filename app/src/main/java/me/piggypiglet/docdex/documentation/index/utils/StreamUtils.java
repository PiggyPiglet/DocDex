package me.piggypiglet.docdex.documentation.index.utils;

import me.piggypiglet.docdex.documentation.index.algorithm.Algorithm;
import me.piggypiglet.docdex.documentation.index.algorithm.AlgorithmOption;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

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

    @NotNull
    public static Stream<String> orderByAlgorithm(@NotNull final Stream<String> strings, @NotNull final String query,
                                                  @NotNull final Algorithm algorithm, @NotNull final AlgorithmOption option) {
        return strings.sorted(Collections.reverseOrder(Comparator.comparingDouble(string -> algorithm.calculate(query, string, option))));
    }
}
