package me.piggypiglet.docdex.config.strategies.maven;

import me.piggypiglet.docdex.config.UpdateStrategy;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MavenLatestStrategy {
    private UpdateStrategy strategy;
    private String path;
    private String repository;
    private String repositoryPath;

    @NotNull
    public UpdateStrategy getStrategy() {
        return strategy;
    }

    @NotNull
    public String getPath() {
        return path;
    }

    @NotNull
    public String getRepository() {
        return repository;
    }

    @NotNull
    public String getRepositoryPath() {
        return repositoryPath;
    }
}
