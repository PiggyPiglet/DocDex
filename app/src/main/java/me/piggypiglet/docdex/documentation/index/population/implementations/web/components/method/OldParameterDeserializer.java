package me.piggypiglet.docdex.documentation.index.population.implementations.web.components.method;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class OldParameterDeserializer {
    private OldParameterDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    static String deserialize(@NotNull final Element details, @NotNull final String name) {
        final String pre = details.selectFirst("pre").text()
                .replace(' ' + name + '(', "汉")
                .replace('\u00a0' + name + '(', "汉")
                .replace(' ' + name + '\u200b' + '(', "汉")
                .replace('\u00a0' + name + '\u200b' + '(', "汉");
        return pre.substring(pre.indexOf('汉') + 1, pre.lastIndexOf(')'));
    }
}
