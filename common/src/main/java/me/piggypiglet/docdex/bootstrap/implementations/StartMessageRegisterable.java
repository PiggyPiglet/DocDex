package me.piggypiglet.docdex.bootstrap.implementations;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class StartMessageRegisterable extends Registerable {
    private static final Logger LOGGER = LoggerFactory.getLogger("DocDex");

    private final Set<CompletableFuture<?>> startupHooks;
    private final long millis;

    @Inject
    public StartMessageRegisterable(@NotNull @Named("startup") final Set<CompletableFuture<?>> startupHooks, @Named("startup") final long millis) {
        this.startupHooks = startupHooks;
        this.millis = millis;
    }

    @Override
    public void execute() {
        final CompletableFuture<?> completableFuture = new CompletableFuture<>();
        startupHooks.add(completableFuture);

        CompletableFuture.allOf(startupHooks.toArray(new CompletableFuture[]{})).whenComplete((v, t) -> {
            final long seconds = (System.currentTimeMillis() - millis) / 1000;
            LOGGER.info("DocDex initialization process complete in " + seconds + " second(s).");
        });

        completableFuture.complete(null);
    }
}
