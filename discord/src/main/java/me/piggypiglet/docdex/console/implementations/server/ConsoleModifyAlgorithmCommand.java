package me.piggypiglet.docdex.console.implementations.server;

import com.google.inject.Inject;
import me.piggypiglet.docdex.db.dbo.DatabaseObjects;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.server.commands.ModifyAlgorithmCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ConsoleModifyAlgorithmCommand extends ConsoleServerCommand {
    @Inject
    public ConsoleModifyAlgorithmCommand(@NotNull final Set<Server> servers, @NotNull final DatabaseObjects adapters) {
        super("algorithm", "Set a server's algorithm.", servers,
                new ModifyAlgorithmCommand("<server> <algorithm>", adapters));
    }
}
