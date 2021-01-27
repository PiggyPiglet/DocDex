package documentation.index;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.piggypiglet.docdex.documentation.index.algorithm.Algorithm;
import me.piggypiglet.docdex.documentation.index.algorithm.AlgorithmOption;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

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
    private static final Map<Algorithm, Map<AlgorithmOption, Multimap<String, String>>> VALUES = new EnumMap<>(Algorithm.class);

    static {
        final Multimap<String, String> simpleRatioLevenshteinValues = ArrayListMultimap.create();
        simpleRatioLevenshteinValues.putAll(SAMPLE_QUERY, List.of("ad", "id", "aliqua", "veniam", "fugiat"));

        VALUES.put(Algorithm.SIMPLE_RATIO_LEVENSHTEIN, Map.of(
                AlgorithmOption.SIMILARITY, simpleRatioLevenshteinValues,
                AlgorithmOption.DISTANCE, simpleRatioLevenshteinValues
        ));

        final Multimap<String, String> normalizedLevenshteinValues = ArrayListMultimap.create();
        normalizedLevenshteinValues.putAll(SAMPLE_QUERY, List.of("ad", "id", "ipsum", "dolor", "sit"));

        VALUES.put(Algorithm.NORMALIZED_LEVENSHTEIN, Map.of(
                AlgorithmOption.SIMILARITY, normalizedLevenshteinValues,
                AlgorithmOption.DISTANCE, normalizedLevenshteinValues
        ));

        final Multimap<String, String> jaroWinklerValues = ArrayListMultimap.create();
        jaroWinklerValues.putAll(SAMPLE_QUERY, List.of("ad", "id", "ea", "in", "veniam"));

        VALUES.put(Algorithm.JARO_WINKLER, Map.of(
                AlgorithmOption.SIMILARITY, jaroWinklerValues,
                AlgorithmOption.DISTANCE, jaroWinklerValues
        ));
    }

    @Test
    public void debug() {
        VALUES.forEach((algorithm, map) -> map.forEach((option, expectedValues) -> expectedValues.asMap().forEach((key, collection) ->
                assertIterableEquals(SAMPLE_SET.stream()
                        .sorted(orderByAlgorithm(key, algorithm, option))
                        .limit(5)
                        .collect(Collectors.toList()), collection)
        )));
    }

    @NotNull
    private static Comparator<String> orderByAlgorithm(@NotNull final String query, @NotNull final Algorithm algorithm,
                                                       @NotNull final AlgorithmOption algorithmOption) {
        return Comparator.comparingDouble(string -> algorithm.calculate(query, string, algorithmOption));
    }
}
