package me.piggypiglet.docdex.documentation.index.population.implementations.web.components.details.method;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

import java.util.Optional;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class NewParameterDeserializer {
    private NewParameterDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    static String deserialize(@NotNull final Element details) {
        return Optional.ofNullable(details.selectFirst(".memberSignature").selectFirst(".arguments"))
                // substring because args span includes trailing )
                .map(arguments -> arguments.text().substring(0, arguments.text().length() - 1))
                .orElse("");
    }
}
