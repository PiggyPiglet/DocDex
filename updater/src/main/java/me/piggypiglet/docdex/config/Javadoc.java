package me.piggypiglet.docdex.config;

import me.piggypiglet.docdex.config.strategies.MavenLatestStrategy;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class Javadoc {
    private Set<String> names;
    private String link;
    private String actualLink;
    private UpdateStrategy strategy;
    private MavenLatestStrategy strategyData;
}
