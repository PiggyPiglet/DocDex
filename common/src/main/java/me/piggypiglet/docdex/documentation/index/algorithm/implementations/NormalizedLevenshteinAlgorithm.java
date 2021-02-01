package me.piggypiglet.docdex.documentation.index.algorithm.implementations;

import me.piggypiglet.docdex.documentation.index.algorithm.StringDistance;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class NormalizedLevenshteinAlgorithm implements StringDistance {
    @Override
    public double calculate(final byte @NotNull [] x, final byte @NotNull [] y) {
        if (Arrays.equals(x, y)) {
            return 0;
        }

        final int maxLength = Math.max(x.length, y.length);

        if (maxLength == 0) {
            return 0;
        }

        return distance(x, y) / maxLength;
    }

    private double distance(final byte @NotNull [] x, final byte @NotNull [] y) {
        if (x.length == 0) {
            return y.length;
        }

        if (y.length == 0) {
            return x.length;
        }

        int[] distances0 = new int[y.length + 1];
        int[] distances1 = new int[y.length + 1];
        int[] temp;

        for (int i = 0; i < distances0.length; ++i) {
            distances0[i] = i;
        }

        for (int i = 0; i < x.length; ++i) {
            distances1[0] = i + 1;

            int minDistance1 = distances1[0];

            for (int j = 0; j < y.length; ++j) {
                int cost = 1;

                if (x[i] == y[i]) {
                    cost = 0;
                }

                distances1[j + 1] = Math.min(
                        distances1[j] + 1,
                        Math.min(distances0[j + 1] + 1, distances0[j] + cost)
                );

                minDistance1 = Math.min(minDistance1, distances1[j + 1]);
            }

            if (minDistance1 >= Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }

            temp = distances0;
            distances0 = distances1;
            distances1 = temp;
        }

        return distances0[y.length];
    }
}
