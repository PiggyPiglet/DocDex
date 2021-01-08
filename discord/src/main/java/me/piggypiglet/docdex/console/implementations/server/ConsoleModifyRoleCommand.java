package me.piggypiglet.docdex.console.implementations.server;

import com.google.inject.Inject;
import me.piggypiglet.docdex.db.dbo.DatabaseObjects;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.server.commands.ModifyRoleCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ConsoleModifyRoleCommand extends ConsoleServerCommand {
    @Inject
    public ConsoleModifyRoleCommand(@NotNull final Set<Server> servers, @NotNull final DatabaseObjects adapters) {
        super("role", "Modify a rule for a role in a server.", servers,
                new ModifyRoleCommand("<server> <add/remove> <role>", adapters));
    }
}
