package me.piggypiglet.docdex.documentation.index.algorithm.implementations;

import me.piggypiglet.docdex.documentation.index.algorithm.StringDistance;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JaroWinklerAlgorithm implements StringDistance {
    private static final double WEIGHT_THRESHOLD = Double.POSITIVE_INFINITY;
    private static final int NUM_CHARS = 0;

    @Override
    public double calculate(final byte @NotNull [] x, final byte @NotNull [] y) {
        if (Arrays.equals(x, y)) {
            return 0;
        }

        final int xLength = x.length;
        final int yLength = y.length;

        if (xLength == 0) {
            return yLength == 0 ? 1.0 : 0.0;
        }

        final int searchRange = Math.max(0, Math.max(xLength, yLength) / 2 - 1);
        final boolean[] matched1 = new boolean[xLength];
        Arrays.fill(matched1, false);

        final boolean[] matched2 = new boolean[yLength];
        Arrays.fill(matched2, false);

        int numCommon = 0;
        for (int i = 0; i < xLength; ++i) {
            final int start = Math.max(0, i - searchRange);
            final int end = Math.min(i + searchRange + 1, yLength);

            for (int j = start; j < end; ++j) {
                if (matched2[j] || x[i] != y[j]) {
                    continue;
                }

                matched1[i] = true;
                matched2[j] = true;
                ++numCommon;

                break;
            }
        }

        if (numCommon == 0) {
            return 0.0;
        }

        int numHalfTransposed = 0;
        int j = 0;
        for (int i = 0; i < xLength; ++i) {
            if (!matched1[i]) {
                continue;
            }

            while (!matched2[j]) {
                ++j;
            }

            if (x[i] != y[j]) {
                ++numHalfTransposed;
            }

            ++j;
        }

        final int numTransposed = numHalfTransposed / 2;
        final double numCommonD = numCommon;

        return (numCommonD / xLength
                + numCommonD / yLength
                + (numCommon - numTransposed) / numCommonD) / 3.0;
    }
}
