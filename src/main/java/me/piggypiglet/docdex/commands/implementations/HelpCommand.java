package me.piggypiglet.docdex.commands.implementations;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.commands.Command;
import me.piggypiglet.docdex.scanning.annotations.Hidden;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Hidden
public final class HelpCommand extends Command {
    private final Set<Command> commands;

    @Inject
    public HelpCommand(@NotNull @Named("commands") final Set<Command> commands) {
        super("help", "this page.");
        this.commands = commands;
    }

    @Override
    public void execute() {
        final StringBuilder helpMessage = new StringBuilder();

        commands.forEach(command -> helpMessage.append("\n").append(command.getPrefix()).append(" - ").append(command.getDescription()));

        msg(helpMessage);
    }
}
