package me.piggypiglet.docdex.documentation.index.algorithm;

import info.debatty.java.stringsimilarity.interfaces.StringDistance;
import info.debatty.java.stringsimilarity.interfaces.StringSimilarity;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DebattyAlgorithm implements AlgorithmFunction {
    private final StringDistance object;

    public DebattyAlgorithm(@NotNull final StringDistance object) {
        this.object = object;
    }

    @Override
    public double calculate(final @NotNull String string1, final @NotNull String string2,
                            final @NotNull AlgorithmOption algorithmOption) {
        if (algorithmOption == AlgorithmOption.SIMILARITY && object instanceof StringSimilarity) {
            return ((StringSimilarity) object).similarity(string1, string2) * -1;
        }

        return object.distance(string1, string2);
    }
}
