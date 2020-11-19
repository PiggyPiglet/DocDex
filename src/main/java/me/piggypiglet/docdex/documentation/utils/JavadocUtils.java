package me.piggypiglet.docdex.documentation.utils;

import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JavadocUtils {
    private JavadocUtils() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    public static String appendUrl(@NotNull String javadocUrl, @NotNull final String appendment) {
        if (javadocUrl.endsWith("/") || javadocUrl.endsWith(".html")) {
            javadocUrl = javadocUrl.substring(javadocUrl.lastIndexOf("/"));
        }

        javadocUrl = javadocUrl + '/' + appendment;

        return javadocUrl;
    }
}
