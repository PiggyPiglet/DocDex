package me.piggypiglet.docdex.console.listener;

import com.google.inject.Inject;
import me.piggypiglet.docdex.console.ConsoleCommandHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ConsoleCommandListener implements Runnable {
    private final ConsoleCommandHandler commandHandler;

    @Inject
    public ConsoleCommandListener(@NotNull final ConsoleCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        final Scanner input = new Scanner(System.in);

        while (true) {
            final String line = input.nextLine();
            commandHandler.process(line);
        }
    }
}
