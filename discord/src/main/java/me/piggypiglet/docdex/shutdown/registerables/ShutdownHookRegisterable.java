package me.piggypiglet.docdex.shutdown.registerables;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.shutdown.ShutdownHook;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ShutdownHookRegisterable extends Registerable {
    private final ShutdownHook shutdownHook;

    @Inject
    public ShutdownHookRegisterable(@NotNull final ShutdownHook shutdownHook) {
        this.shutdownHook = shutdownHook;
    }

    @Override
    protected void execute() {
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }
}
