package me.piggypiglet.docdex.bootstrap;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.commands.registerables.CommandsRegisterable;
import me.piggypiglet.docdex.commands.registerables.ConsoleCommandsRegisterable;
import me.piggypiglet.docdex.documentation.registerables.IndexPopulationRegisterable;
import me.piggypiglet.docdex.file.registerables.FileObjectsRegisterable;
import me.piggypiglet.docdex.file.registerables.FilesRegisterable;
import me.piggypiglet.docdex.guice.ExceptionalInjector;
import me.piggypiglet.docdex.guice.modules.InitialModule;
import me.piggypiglet.docdex.http.registerables.RoutesRegisterable;
import me.piggypiglet.docdex.http.registerables.ServerRegisterable;
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
            FileObjectsRegisterable.class,
            FilesRegisterable.class,

            CommandsRegisterable.class,
            ConsoleCommandsRegisterable.class,

            IndexPopulationRegisterable.class,

            RoutesRegisterable.class,
            ServerRegisterable.class,

            ShutdownHookRegisterable.class
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
