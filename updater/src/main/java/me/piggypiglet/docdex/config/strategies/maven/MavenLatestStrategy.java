package me.piggypiglet.docdex.config.strategies.maven;

import me.piggypiglet.docdex.config.UpdateStrategyType;
import me.piggypiglet.docdex.config.strategies.UpdateStrategy;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MavenLatestStrategy implements UpdateStrategy {
    private UpdateStrategyType type;
    private String path;
    private String artifactLink;

    @NotNull
    public UpdateStrategyType getType() {
        return type;
    }

    @NotNull
    public String getPath() {
        return path;
    }

    @NotNull
    public String getArtifactLink() {
        return artifactLink;
    }
}
