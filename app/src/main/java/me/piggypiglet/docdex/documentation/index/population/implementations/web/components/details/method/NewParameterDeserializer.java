package me.piggypiglet.docdex.documentation.index.population.implementations.web.components.details.method;

import me.piggypiglet.docdex.documentation.objects.detail.method.DocumentedMethodBuilder;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

import java.util.Arrays;
import java.util.Optional;

import static me.piggypiglet.docdex.documentation.index.population.implementations.web.components.details.method.MethodDeserializer.LINE_DELIMITER;
import static me.piggypiglet.docdex.documentation.index.population.implementations.web.components.details.method.MethodDeserializer.LIST_DELIMITER;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class NewParameterDeserializer {
    private NewParameterDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    static void deserialize(@NotNull final Element details, @NotNull final DocumentedMethodBuilder builder) {
        final Element signature = details.selectFirst(".memberSignature");
        Optional.ofNullable(signature.selectFirst(".arguments")).ifPresent(arguments ->
                Arrays.stream(LIST_DELIMITER.split(LINE_DELIMITER.matcher(arguments.text().replace(")", "")).replaceAll(" ")))
                        .map(String::trim)
                        .forEach(builder::parameters));
    }
}
