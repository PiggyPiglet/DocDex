package documentation.index;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.piggypiglet.docdex.documentation.index.algorithm.Algorithm;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class IndexAlgorithmTests {
    private static final String SAMPLE_QUERY = "kwiad hjk";
    private static final List<String> SAMPLE_SET = List.of(
            "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing",
            "elit", "sed", "do", "eiusmod", "tempor", "incididunt", "ut",
            "labore", "et", "dolore", "magna", "aliqua", "enim", "ad", "minim",
            "veniam", "quis", "nostrud", "exercitation", "ullamco", "laboris",
            "nisi", "aliquip", "ex", "ea", "commodo", "consequat", "duis",
            "aute", "irure", "in", "reprehenderit", "voluptate", "velit", "esse",
            "cillum", "eu", "fugiat", "nulla", "pariatur", "excepteur", "sint",
            "occaecat", "cupidatat", "non", "proident", "sunt", "culpa", "qui",
            "officia", "deserunt", "mollit", "anim", "id", "est", "laborum"
    );
    private static final Map<Algorithm, Multimap<String, String>> VALUES = new EnumMap<>(Algorithm.class);

    static {
        final Multimap<String, String> normalizedLevenshteinValues = ArrayListMultimap.create();
        normalizedLevenshteinValues.putAll(SAMPLE_QUERY, List.of("ad", "id", "ipsum", "dolor", "sit"));

        VALUES.put(Algorithm.NORMALIZED_LEVENSHTEIN, normalizedLevenshteinValues);

        final Multimap<String, String> jaroWinklerValues = ArrayListMultimap.create();
        jaroWinklerValues.putAll(SAMPLE_QUERY, List.of("ad", "id", "ea", "in", "veniam"));

        VALUES.put(Algorithm.JARO_WINKLER, jaroWinklerValues);
    }

    @Test
    public void debug() {
        VALUES.forEach((algorithm, map) -> map.asMap().forEach((query, expectedValues) ->
                assertIterableEquals(SAMPLE_SET.stream()
                        .sorted(orderByAlgorithm(query, algorithm))
                        .limit(5)
                        .collect(Collectors.toList()), expectedValues)
        ));
    }

    @NotNull
    private static Comparator<String> orderByAlgorithm(@NotNull final String query, @NotNull final Algorithm algorithm) {
        return Comparator.comparingDouble(string -> algorithm.calculate(query.getBytes(StandardCharsets.US_ASCII), string.getBytes(StandardCharsets.US_ASCII)));
    }
}
