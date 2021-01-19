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
    private static final Pattern EXCESS_WHITESPACE = Pattern.compile("\\s\\s+");

    private DeserializationUtils() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    public static String generateFqn(@NotNull final Element element) {
        return ANCHOR_TITLE_PACKAGE_DELIMITER.split(element.attr("title"))[1] + '.' + element.text();
    }

    @NotNull
    public static String killChars(@NotNull final String string, final char prefix,
                                    final char suffix) {
        final StringBuilder builder = new StringBuilder();

        int count = 0;
        for (int i = 0; i < string.length(); ++i) {
            final char character = string.charAt(i);

            if (character == prefix) {
                ++count;
            }

            if (character == suffix && --count == 0) {
                continue;
            }

            if (count != 0) {
                continue;
            }

            builder.append(character);
        }

        return builder.toString();
    }

    @NotNull
    public static String removeExcessWhitespace(@NotNull final String string) {
        return EXCESS_WHITESPACE.matcher(string).replaceAll(" ");
    }
}
