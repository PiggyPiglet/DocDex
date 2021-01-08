package me.piggypiglet.docdex.console.registerables;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.console.listener.ConsoleCommandListener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ConsoleCommandListenerRegisterable extends Registerable {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private final ConsoleCommandListener listener;

    @Inject
    public ConsoleCommandListenerRegisterable(@NotNull final ConsoleCommandListener listener) {
        this.listener = listener;
    }

    @Override
    public void execute() {
        EXECUTOR.execute(listener);
    }
}
