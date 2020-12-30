package me.piggypiglet.docdex.documentation.index.population.implementations.web.utils;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

import java.util.regex.Pattern;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DeserializationUtils {
    private static final Pattern ANCHOR_TITLE_PACKAGE_DELIMITER = Pattern.compile(" in ");

    private DeserializationUtils() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    public static String generateFqn(@NotNull final Element element) {
        return ANCHOR_TITLE_PACKAGE_DELIMITER.split(element.attr("title"))[1] + '.' + element.text();
    }
}
