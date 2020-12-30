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
public final class ModifyPrefixCommand extends ServerCommand {
    public ModifyPrefixCommand(@NotNull final String usage, @NotNull final DatabaseObjects adapters) {
        super(usage, 2, adapters);
    }

    @Override
    protected void execute(@NotNull final Server server, @NotNull final List<String> args,
                           @NotNull final Consumer<String> messageFunction) {
        server.setPrefix(args.get(1));
        messageFunction.accept("Successfully set " + server.getId() + "'s prefix to " + args.get(1));
    }
}
