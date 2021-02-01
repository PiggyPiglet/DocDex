package me.piggypiglet.docdex.documentation.index.utils;

import me.piggypiglet.docdex.documentation.index.algorithm.Algorithm;
import org.jetbrains.annotations.NotNull;

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
    public static Stream<byte[]> orderByAlgorithm(@NotNull final Stream<byte[]> elements, final byte @NotNull [] query,
                                                  @NotNull final Algorithm algorithm) {
        return elements
                .map(bytes -> new CachedElement(bytes, algorithm.calculate(query, bytes)))
                .parallel()
                .sorted(Comparator.comparingDouble(CachedElement::getScore))
                .map(CachedElement::getContent);
    }

    private static final class CachedElement {
        private final byte[] content;
        private final double score;

        public CachedElement(final byte @NotNull [] content, final double score) {
            this.content = content;
            this.score = score;
        }

        public byte @NotNull [] getContent() {
            return content;
        }

        public double getScore() {
            return score;
        }
    }
}
