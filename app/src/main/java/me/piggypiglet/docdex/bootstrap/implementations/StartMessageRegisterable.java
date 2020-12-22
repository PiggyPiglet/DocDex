package me.piggypiglet.docdex.bootstrap.implementations;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.documentation.index.data.population.registerables.IndexPopulationRegisterable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class StartMessageRegisterable extends Registerable {
    private static final Logger LOGGER = LoggerFactory.getLogger("DocDex");

    private final IndexPopulationRegisterable indexPopulationRegisterable;
    private final long millis;

    @Inject
    public StartMessageRegisterable(@NotNull final IndexPopulationRegisterable indexPopulationRegisterable, @Named("startup") final long millis) {
        this.indexPopulationRegisterable = indexPopulationRegisterable;
        this.millis = millis;
    }

    @Override
    public void execute() {
        indexPopulationRegisterable.getCompleted().whenComplete((v, t) -> {
            final long seconds = (System.currentTimeMillis() - millis) / 1000;
            LOGGER.info("DocDex initialization process complete in " + seconds + " second(s).");
        });
    }
}
