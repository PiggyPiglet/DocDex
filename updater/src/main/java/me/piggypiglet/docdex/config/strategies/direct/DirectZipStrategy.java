package me.piggypiglet.docdex.config.strategies.direct;

import me.piggypiglet.docdex.config.strategies.UpdateStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DirectZipStrategy implements UpdateStrategy {
    private String path;
    private String zip;
    private String link;
    private String innerPath;

    @NotNull
    @Override
    public String getPath() {
        return path;
    }

    @NotNull
    @Override
    public String getZip() {
        return zip;
    }

    @NotNull
    public String getLink() {
        return link;
    }

    @Nullable
    public String getInnerPath() {
        return innerPath;
    }
}
