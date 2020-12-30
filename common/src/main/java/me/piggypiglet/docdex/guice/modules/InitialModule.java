package me.piggypiglet.docdex.guice.modules;

import com.google.inject.AbstractModule;
import me.piggypiglet.docdex.bootstrap.DocDexBootstrap;
import me.piggypiglet.docdex.scanning.framework.Scanner;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class InitialModule extends AbstractModule {
    private final DocDexBootstrap main;
    private final Scanner scanner;

    public InitialModule(@NotNull final DocDexBootstrap main, @NotNull final Scanner scanner) {
        this.main = main;
        this.scanner = scanner;
    }

    @Override
    protected void configure() {
        bind(DocDexBootstrap.class).toInstance(main);
        bind(Scanner.class).toInstance(scanner);
    }
}
