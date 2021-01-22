package me.piggypiglet.docdex.documentation.index.algorithm;

import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public enum AlgorithmOption {
    DISTANCE,
    SIMILARITY;

    @Unmodifiable
    public static final Map<String, AlgorithmOption> NAMES = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(Enum::name, algorithmOption -> algorithmOption));
}
