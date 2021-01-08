package me.piggypiglet.docdex.bot.commands.implementations.server;

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
public final class BotModifyRoleCommand extends BotServerCommand {
    private static final String USAGE = "<add/remove> <role>";

    @Inject
    public BotModifyRoleCommand(@NotNull final Set<Server> servers, @NotNull final DatabaseObjects adapters) {
        super(Set.of("role"), USAGE, "Modify a role's access to admin commands in this server.", servers,
                new ModifyRoleCommand(USAGE, adapters));
    }
}
