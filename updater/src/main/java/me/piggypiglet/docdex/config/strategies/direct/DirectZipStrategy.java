package me.piggypiglet.docdex.config.strategies.direct;

import me.piggypiglet.docdex.config.strategies.UpdateStrategy;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DirectZipStrategy implements UpdateStrategy {
    private final String path;
    private final String link;

    public DirectZipStrategy(@NotNull final String path, @NotNull final String link) {
        this.path = path;
        this.link = link;
    }

    @NotNull
    @Override
    public String getPath() {
        return path;
    }

    @NotNull
    public String getLink() {
        return link;
    }
}
