package me.piggypiglet.docdex.documentation.index.algorithm.implementations.jaro;

import me.piggypiglet.docdex.documentation.index.algorithm.StringDistance;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DukeJaroWinklerAlgorithm implements StringDistance {
    @Override
    public double calculate(final byte @NotNull [] x, final byte @NotNull [] y) {
        if (Arrays.equals(x, y)) {
            return 0;
        }

        final byte[] min;
        final byte[] max;

        if (x.length > y.length) {
            min = y;
            max = x;
        } else {
            min = x;
            max = y;
        }

        final int minLength = min.length;
        final int maxLength = max.length;

        final int maxDistance = maxLength / 2;

        int commonCharacters = 0;
        int transpositions = 0;
        int previousPosition = -1;
        for (int minIndex = 0; minIndex < minLength; ++minIndex) {
            final byte character = min[minIndex];

            for (int maxIndex = Math.max(0, minIndex - maxDistance);
                 maxIndex < Math.min(maxLength, minIndex + maxDistance);
                 ++maxIndex) {
                if (character == max[maxIndex]) {
                    ++commonCharacters;

                    if (previousPosition != -1 && maxIndex < previousPosition) {
                        ++transpositions;
                    }

                    previousPosition = maxIndex;
                    break;
                }
            }
        }

        if (commonCharacters == 0) {
            return 0;
        }

        double score = ((commonCharacters / (double) minLength) +
                (commonCharacters / (double) maxLength) +
                ((commonCharacters - transpositions) / (double) commonCharacters)) / 3D;
        final int last = Math.min(4, minLength);

        int prefix = 0;
        while (prefix < last && min[prefix] == max[prefix]) {
            ++prefix;
        }

        score = score + ((prefix * (1 - score)) / 10);
        return score;
    }
}
