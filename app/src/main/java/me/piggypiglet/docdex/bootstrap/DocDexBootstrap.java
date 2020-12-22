package me.piggypiglet.docdex.bootstrap;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.bootstrap.implementations.StartMessageRegisterable;
import me.piggypiglet.docdex.bootstrap.implementations.StartupTimeRegisterable;
import me.piggypiglet.docdex.console.registerables.ConsoleCommandListenerRegisterable;
import me.piggypiglet.docdex.console.registerables.ConsoleCommandsRegisterable;
import me.piggypiglet.docdex.db.registerables.MongoRegisterable;
import me.piggypiglet.docdex.documentation.index.data.population.registerables.IndexPopulationRegisterable;
import me.piggypiglet.docdex.documentation.index.data.population.registerables.IndexPopulatorRegisterable;
import me.piggypiglet.docdex.documentation.index.data.storage.registerables.IndexStorageRegisterable;
import me.piggypiglet.docdex.file.registerables.FileObjectsRegisterable;
import me.piggypiglet.docdex.file.registerables.FilesRegisterable;
import me.piggypiglet.docdex.guice.ExceptionalInjector;
import me.piggypiglet.docdex.guice.modules.InitialModule;
import me.piggypiglet.docdex.http.registerables.RoutesRegisterable;
import me.piggypiglet.docdex.http.registerables.ServerRegisterable;
import me.piggypiglet.docdex.logging.JULToSLF4JRegisterable;
import me.piggypiglet.docdex.shutdown.registerables.ShutdownHookRegisterable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DocDexBootstrap {
    private static final List<Class<? extends Registerable>> REGISTERABLES = Lists.newArrayList(
            StartupTimeRegisterable.class,

            JULToSLF4JRegisterable.class,

            FileObjectsRegisterable.class,
            FilesRegisterable.class,

            ConsoleCommandsRegisterable.class,
            ConsoleCommandListenerRegisterable.class,

            MongoRegisterable.class,

            IndexStorageRegisterable.class,
            IndexPopulatorRegisterable.class,
            IndexPopulationRegisterable.class,

            RoutesRegisterable.class,
            ServerRegisterable.class,

            ShutdownHookRegisterable.class,

            StartMessageRegisterable.class
    );

    private final AtomicReference<Injector> injector = new AtomicReference<>();

    public static void main(String[] args) {
        final DocDexBootstrap main = new DocDexBootstrap();
        final InitialModule initialModule = new InitialModule(main);
        final AtomicReference<Injector> injector = main.injector;

        injector.set(new ExceptionalInjector(Guice.createInjector(initialModule)));

        for (final Class<? extends Registerable> registerableClass : REGISTERABLES) {
            final Registerable registerable = injector.get().getInstance(registerableClass);
            registerable.run(injector.get());

            registerable.createModule()
                    .map(injector.get()::createChildInjector)
                    .ifPresent(injector::set);
        }
    }

    @NotNull
    public Injector getInjector() {
        return injector.get();
    }
}
