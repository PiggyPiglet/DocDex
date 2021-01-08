package me.piggypiglet.docdex.console.implementations;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.console.ConsoleCommand;
import me.piggypiglet.docdex.scanning.annotations.Hidden;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Hidden
public final class HelpCommand extends ConsoleCommand {
    private final Set<ConsoleCommand> commands;

    @Inject
    public HelpCommand(@NotNull @Named("console commands") final Set<ConsoleCommand> commands) {
        super("help", "this page.");
        this.commands = commands;
    }

    @Override
    public void execute() {
        final StringBuilder helpMessage = new StringBuilder();

        commands.forEach(command -> helpMessage.append("\n").append(command.getMatch()).append(" - ").append(command.getDescription()));

        msg(helpMessage);
    }
}
