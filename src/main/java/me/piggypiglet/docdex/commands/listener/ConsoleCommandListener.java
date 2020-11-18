package me.piggypiglet.docdex.commands.listener;

import com.google.inject.Inject;
import me.piggypiglet.docdex.commands.CommandHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ConsoleCommandListener implements Runnable {
    private final CommandHandler commandHandler;

    @Inject
    public ConsoleCommandListener(@NotNull final CommandHandler commandHandler) {
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
