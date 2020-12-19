package me.piggypiglet.docdex.console.implementations;

import me.piggypiglet.docdex.console.ConsoleCommand;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class GarbageCollectionCommand extends ConsoleCommand {
    public GarbageCollectionCommand() {
        super("gc", "You shouldn't really use this command, but it's here anyway. It removes useless shit from memory.");
    }

    @Override
    public void execute() {
        final long memory = Runtime.getRuntime().freeMemory();
        System.gc();
        msg("Wow we just cleared " + (memory - Runtime.getRuntime().freeMemory()) / 1_000_000 + "MB from memory.");
    }
}
