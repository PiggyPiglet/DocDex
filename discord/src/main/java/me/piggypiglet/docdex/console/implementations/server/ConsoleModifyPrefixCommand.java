package me.piggypiglet.docdex.console.implementations.server;

import com.google.inject.Inject;
import me.piggypiglet.docdex.db.adapters.DatabaseObjectAdapters;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.server.commands.ModifyPrefixCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ConsoleModifyPrefixCommand extends ConsoleServerCommand {
    @Inject
    public ConsoleModifyPrefixCommand(@NotNull final Set<Server> servers, @NotNull final DatabaseObjectAdapters adapters) {
        super("prefix", "Set a server's prefix.", servers,
                new ModifyPrefixCommand("<server> <prefix>", adapters));
    }
}
