package me.piggypiglet.docdex.documentation.index.algorithm;

import info.debatty.java.stringsimilarity.JaroWinkler;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import info.debatty.java.stringsimilarity.interfaces.StringDistance;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public enum Algorithm implements AlgorithmFunction {
    SIMPLE_RATIO_LEVENSHTEIN((AlgorithmFunctionWithoutOption) (string1, string2) -> FuzzySearch.ratio(string1, string2) * -1),
//    PARTIAL_RATIO_LEVENSHTEIN((AlgorithmFunctionWithoutOption) FuzzySearch::partialRatio),
//    TOKEN_SORT_RATIO_LEVENSHTEIN((AlgorithmFunctionWithoutOption) FuzzySearch::tokenSortRatio),
//    TOKEN_SET_RATIO_LEVENSHTEIN((AlgorithmFunctionWithoutOption) FuzzySearch::tokenSetRatio),
    NORMALIZED_LEVENSHTEIN(new NormalizedLevenshtein()),
    JARO_WINKLER(new JaroWinkler());

    @Unmodifiable
    public static final Map<String, Algorithm> NAMES = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(Enum::name, constant -> constant));

    private final AlgorithmFunction function;

    Algorithm(@NotNull final AlgorithmFunction function) {
        this.function = function;
    }

    Algorithm(@NotNull final AlgorithmFunctionWithoutOption function) {
        this((string1, string2, algorithmOption) -> function.calculate(string1, string2));
    }

    Algorithm(@NotNull final StringDistance object) {
        this(new DebattyAlgorithm(object));
    }

    @Override
    public double calculate(@NotNull final String string1, @NotNull final String string2,
                            @NotNull final AlgorithmOption algorithmOption) {
        return function.calculate(string1, string2, algorithmOption);
    }
}
