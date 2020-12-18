package me.piggypiglet.docdex.guice.modules;

import com.google.inject.AbstractModule;
import me.piggypiglet.docdex.bootstrap.DocDexBootstrap;
import me.piggypiglet.docdex.scanning.framework.Scanner;
import me.piggypiglet.docdex.scanning.implementations.ZISScanner;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class InitialModule extends AbstractModule {
    private static final Class<DocDexBootstrap> MAIN_CLASS = DocDexBootstrap.class;
    private static final String PACKAGE = "me/piggypiglet/docdex";

    private final DocDexBootstrap main;

    public InitialModule(@NotNull final DocDexBootstrap main) {
        this.main = main;
    }

    @Override
    protected void configure() {
        bind(DocDexBootstrap.class).toInstance(main);
        bind(Scanner.class).toInstance(ZISScanner.create(MAIN_CLASS, PACKAGE));
    }
}
