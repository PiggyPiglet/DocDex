package me.piggypiglet.docdex.documentation.index.algorithm;

import me.piggypiglet.docdex.documentation.index.algorithm.implementations.jaro.DebattyJaroWinklerAlgorithm;
import me.piggypiglet.docdex.documentation.index.algorithm.implementations.NormalizedLevenshteinAlgorithm;
import me.piggypiglet.docdex.documentation.index.algorithm.implementations.jaro.DukeJaroWinklerAlgorithm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public enum Algorithm implements StringDistance {
    NORMALIZED_LEVENSHTEIN(new NormalizedLevenshteinAlgorithm()),
    DEBATTY_JARO_WINKLER(new DebattyJaroWinklerAlgorithm()),
    DUKE_JARO_WINKLER(new DukeJaroWinklerAlgorithm());

    @Unmodifiable
    public static final Map<String, Algorithm> NAMES = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(Enum::name, constant -> constant));

    private final StringDistance distance;

    Algorithm(@NotNull final StringDistance distance) {
        this.distance = distance;
    }

    @Override
    public double calculate(final byte @NotNull [] x, final byte @NotNull [] y) {
        return -1 * distance.calculate(x, y);
    }
}
