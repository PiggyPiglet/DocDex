package me.piggypiglet.docdex.console.implementations.server;

import me.piggypiglet.docdex.console.ConsoleCommand;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.server.commands.ServerCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class ConsoleServerCommand extends ConsoleCommand {
    private final Set<Server> servers;
    private final ServerCommand command;

    protected ConsoleServerCommand(final @NotNull String match, final @NotNull String description,
                                   @NotNull final Set<Server> servers, @NotNull final ServerCommand command) {
        super(match, description);
        this.servers = servers;
        this.command = command;
    }

    @Override
    protected void execute(final @NotNull List<String> args) {
        if (args.size() < 1 || args.get(0).isBlank()) {
            command.sendUsage(ConsoleCommand::msg);
            return;
        }

        servers.stream()
                .filter(server -> server.getId().equals(args.get(0)))
                .findAny()
                .ifPresentOrElse(
                        server -> command.run(server, args, ConsoleCommand::msg),
                        () -> msg("That server does not exist.")
                );
    }
}
