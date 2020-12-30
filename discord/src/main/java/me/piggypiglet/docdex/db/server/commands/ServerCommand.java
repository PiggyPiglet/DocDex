package me.piggypiglet.docdex.db.server.commands;

import me.piggypiglet.docdex.db.dbo.DatabaseObjects;
import me.piggypiglet.docdex.db.server.Server;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class ServerCommand {
    private final String usage;
    private final int args;
    private final DatabaseObjects adapters;

    protected ServerCommand(@NotNull final String usage, final int args,
                            @NotNull final DatabaseObjects adapters) {
        this.usage = usage;
        this.args = args;
        this.adapters = adapters;
    }

    protected abstract void execute(@NotNull final Server server, @NotNull final List<String> args,
                                    @NotNull final Consumer<String> messageFunction);

    public void run(@NotNull final Server server, final @NotNull List<String> args,
                    @NotNull final Consumer<String> messageFunction) {
        if (args.size() < this.args || args.get(0).isBlank()) {
            sendUsage(messageFunction);
            return;
        }

        execute(server, args, messageFunction);
        adapters.save(server);
    }

    public void sendUsage(@NotNull final Consumer<String> messageFunction) {
        messageFunction.accept("Incorrect usage, use the command as follows: " + usage);
    }
}
