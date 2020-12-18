package me.piggypiglet.docdex.commands.registerables;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.commands.listener.ConsoleCommandListener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ConsoleCommandsRegisterable extends Registerable {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private final ConsoleCommandListener listener;

    @Inject
    public ConsoleCommandsRegisterable(@NotNull final ConsoleCommandListener listener) {
        this.listener = listener;
    }

    @Override
    protected void execute() {
        EXECUTOR.execute(listener);
    }
}
