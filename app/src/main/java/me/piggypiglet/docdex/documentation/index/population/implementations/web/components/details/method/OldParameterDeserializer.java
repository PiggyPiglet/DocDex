package me.piggypiglet.docdex.documentation.index.population.implementations.web.components.details.method;

import me.piggypiglet.docdex.documentation.objects.detail.method.DocumentedMethodBuilder;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

import java.util.Arrays;

import static me.piggypiglet.docdex.documentation.index.population.implementations.web.components.details.method.MethodDeserializer.LIST_DELIMITER;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class OldParameterDeserializer {
    private OldParameterDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    public static void deserialize(@NotNull final Element details, @NotNull final DocumentedMethodBuilder builder) {
        final String pre = details.selectFirst("pre").text();

        Arrays.stream(LIST_DELIMITER.split(pre.substring(pre.indexOf('(') + 1, pre.indexOf(')')).replace("\n", "")))
                .filter(param -> !param.isBlank())
                .map(String::trim)
                .forEach(builder::parameters);
    }
}
