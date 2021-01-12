package me.piggypiglet.docdex.bootstrap;

import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.db.registerables.MongoRegisterable;
import me.piggypiglet.docdex.documentation.index.population.registerables.IndexPopulationRegisterable;
import me.piggypiglet.docdex.documentation.index.population.registerables.IndexPopulatorRegisterable;
import me.piggypiglet.docdex.documentation.index.storage.registerables.IndexStorageRegisterable;
import me.piggypiglet.docdex.http.registerables.RoutesRegisterable;
import me.piggypiglet.docdex.http.registerables.ServerRegisterable;
import me.piggypiglet.docdex.shutdown.registerables.ShutdownHookRegisterable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class AppBootstrap extends DocDexBootstrap {
    private static final List<Class<? extends Registerable>> REGISTERABLES = List.of(
            MongoRegisterable.class,

            IndexStorageRegisterable.class,
            IndexPopulatorRegisterable.class,
            IndexPopulationRegisterable.class,

            RoutesRegisterable.class,
            ServerRegisterable.class,

            ShutdownHookRegisterable.class
    );

    @Override
    protected @NotNull List<Class<? extends Registerable>> provideRegisterables() {
        return REGISTERABLES;
    }
}
