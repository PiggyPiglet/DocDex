package me.piggypiglet.docdex.console.implementations;

import me.piggypiglet.docdex.console.ConsoleCommand;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class StopCommand extends ConsoleCommand {
    public StopCommand() {
        super("stop", "stops the program.");
    }

    @Override
    public void execute() {
        System.exit(0);
    }
}
