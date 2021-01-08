package me.piggypiglet.docdex.documentation;

import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class IndexURLBuilder {
    boolean start = true;
    private final StringBuilder builder = new StringBuilder()
            .append("/index");

    @NotNull
    public IndexURLBuilder javadoc(@NotNull final String value) {
        builder.append(delimiter()).append("javadoc=").append(value);
        return this;
    }

    @NotNull
    public IndexURLBuilder query(@NotNull final String value) {
        builder.append(delimiter()).append("query=").append(formatQuery(value));
        return this;
    }

    @NotNull
    public IndexURLBuilder limit(final int value) {
        builder.append(delimiter()).append("limit=").append(value);
        return this;
    }

    @NotNull
    public String build() {
        return builder.toString();
    }

    private char delimiter() {
        if (start) {
            start = false;
            return '?';
        }

        return '&';
    }

    @NotNull
    private static String formatQuery(@NotNull final String query) {
        return query.replace("#", "~").replace("%", "-");
    }
}
