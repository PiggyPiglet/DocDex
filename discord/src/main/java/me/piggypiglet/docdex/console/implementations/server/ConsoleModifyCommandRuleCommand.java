package me.piggypiglet.docdex.console.implementations.server;

import com.google.inject.Inject;
import me.piggypiglet.docdex.db.adapters.DatabaseObjectAdapters;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.server.commands.ModifyCommandRuleCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ConsoleModifyCommandRuleCommand extends ConsoleServerCommand {
    @Inject
    public ConsoleModifyCommandRuleCommand(@NotNull final Set<Server> servers, @NotNull final DatabaseObjectAdapters adapters) {
        super("command", "Edit rules for a command in a server.", servers,
                new ModifyCommandRuleCommand("<server> <allow/disallow/recommendation> [add/remove] <command> <value>", adapters));
    }
}
