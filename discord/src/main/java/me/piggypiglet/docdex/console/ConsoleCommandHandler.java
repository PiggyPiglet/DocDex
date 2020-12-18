package me.piggypiglet.docdex.console;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.console.implementations.HelpCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class ConsoleCommandHandler {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);

    private final Set<ConsoleCommand> commands;
    private final ConsoleCommand unknownCommand;

    @Inject
    public ConsoleCommandHandler(@NotNull @Named("console commands") final Set<ConsoleCommand> commands, @NotNull final HelpCommand unknownCommand) {
        this.commands = commands;
        this.unknownCommand = unknownCommand;

        commands.add(unknownCommand);
    }

    public void process(@NotNull final String text) {
        final ConsoleCommand command = commands.stream()
                .filter(cmd -> cmd.getMatch().equalsIgnoreCase(text))
                .findAny().orElse(unknownCommand);

        EXECUTOR.submit(command::execute);
    }

    @NotNull
    public Set<ConsoleCommand> getCommands() {
        return commands;
    }
}
