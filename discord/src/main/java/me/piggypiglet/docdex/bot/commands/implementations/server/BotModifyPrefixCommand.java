package me.piggypiglet.docdex.bot.commands.implementations.server;

import com.google.inject.Inject;
import me.piggypiglet.docdex.db.dbo.DatabaseObjects;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.server.commands.ModifyPrefixCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class BotModifyPrefixCommand extends BotServerCommand {
    private static final String USAGE = "<prefix>";

    @Inject
    public BotModifyPrefixCommand(@NotNull final Set<Server> servers, @NotNull final DatabaseObjects adapters) {
        super(Set.of("prefix"), USAGE, "Set the server's command prefix.", servers,
                new ModifyPrefixCommand(USAGE, adapters));
    }
}
