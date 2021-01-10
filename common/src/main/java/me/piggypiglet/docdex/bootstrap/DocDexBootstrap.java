package me.piggypiglet.docdex.bootstrap;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.bootstrap.implementations.StartMessageRegisterable;
import me.piggypiglet.docdex.bootstrap.implementations.StartupHookRegisterable;
import me.piggypiglet.docdex.console.registerables.ConsoleCommandListenerRegisterable;
import me.piggypiglet.docdex.console.registerables.ConsoleCommandsRegisterable;
import me.piggypiglet.docdex.file.registerables.FileObjectsRegisterable;
import me.piggypiglet.docdex.file.registerables.FilesRegisterable;
import me.piggypiglet.docdex.guice.ExceptionalInjector;
import me.piggypiglet.docdex.guice.modules.InitialModule;
import me.piggypiglet.docdex.logging.JULToSLF4JRegisterable;
import me.piggypiglet.docdex.scanning.framework.Scanner;
import me.piggypiglet.docdex.scanning.implementations.ZISScanner;
import me.piggypiglet.docdex.scanning.rules.Rules;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class DocDexBootstrap {
    private static final Class<DocDexBootstrap> MAIN_CLASS = DocDexBootstrap.class;
    private static final String PACKAGE = "me/piggypiglet/docdex";

    private static final List<Class<? extends Registerable>> INITIAL_REGISTERABLES = List.of(
            StartupHookRegisterable.class,

            JULToSLF4JRegisterable.class,

            FileObjectsRegisterable.class,
            FilesRegisterable.class
    );

    private static final List<Class<? extends Registerable>> LAST_REGISTERABLES = List.of(
            ConsoleCommandsRegisterable.class,
            ConsoleCommandListenerRegisterable.class,
            StartMessageRegisterable.class
    );

    @NotNull
    protected abstract List<Class<? extends Registerable>> provideRegisterables();

    public static void main(String[] args) {
        final Scanner scanner = ZISScanner.create(MAIN_CLASS, PACKAGE);
        final DocDexBootstrap main = scanner.getClasses(Rules.builder().typeExtends(DocDexBootstrap.class).disallowMutableClasses().build())
                .map(clazz -> {
                    try {
                        return (DocDexBootstrap) clazz.getConstructors()[0].newInstance();
                    } catch (Exception exception) {
                        throw new AssertionError("Something went wrong when instantiating the main class.", exception);
                    }
                }).findAny().orElseThrow(() -> new AssertionError("Could not find a main class."));
        final InitialModule initialModule = new InitialModule(main, scanner);
        final AtomicReference<Injector> injector = new AtomicReference<>(new ExceptionalInjector(Guice.createInjector(initialModule)));

        final List<Class<? extends Registerable>> registerables = new ArrayList<>();
        registerables.addAll(INITIAL_REGISTERABLES);
        registerables.addAll(main.provideRegisterables());
        registerables.addAll(LAST_REGISTERABLES);

        for (final Class<? extends Registerable> registerableClass : registerables) {
            final Registerable registerable = injector.get().getInstance(registerableClass);
            registerable.run(injector.get());

            registerable.createModule()
                    .map(injector.get()::createChildInjector)
                    .ifPresent(injector::set);
        }
    }
}
