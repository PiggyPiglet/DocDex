package documentation.index;

import me.piggypiglet.docdex.documentation.index.algorithm.Algorithm;
import org.jetbrains.annotations.NotNull;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public class IndexAlgorithmBenchmarks {
    private static final byte[] SAMPLE_QUERY = "kwiad hjk".getBytes(StandardCharsets.US_ASCII);
    private static final List<byte[]> SAMPLE_SET = Stream.of(
            "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing",
            "elit", "sed", "do", "eiusmod", "tempor", "incididunt", "ut",
            "labore", "et", "dolore", "magna", "aliqua", "enim", "ad", "minim",
            "veniam", "quis", "nostrud", "exercitation", "ullamco", "laboris",
            "nisi", "aliquip", "ex", "ea", "commodo", "consequat", "duis",
            "aute", "irure", "in", "reprehenderit", "voluptate", "velit", "esse",
            "cillum", "eu", "fugiat", "nulla", "pariatur", "excepteur", "sint",
            "occaecat", "cupidatat", "non", "proident", "sunt", "culpa", "qui",
            "officia", "deserunt", "mollit", "anim", "id", "est", "laborum"
    ).map(string -> string.getBytes(StandardCharsets.US_ASCII)).collect(Collectors.toUnmodifiableList());

    @Benchmark
    public void jaroWinklerTest(@NotNull final Blackhole blackhole) {
        for (final byte[] bytes : SAMPLE_SET) {
            blackhole.consume(Algorithm.DUKE_JARO_WINKLER.calculate(SAMPLE_QUERY, bytes));
        }
    }
}
