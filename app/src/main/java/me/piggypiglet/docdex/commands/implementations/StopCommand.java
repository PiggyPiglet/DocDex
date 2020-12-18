package me.piggypiglet.docdex.commands.implementations;

import me.piggypiglet.docdex.commands.Command;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class StopCommand extends Command {
    public StopCommand() {
        super("stop", "stops the program.");
    }

    @Override
    public void execute() {
        System.exit(0);
    }
}
