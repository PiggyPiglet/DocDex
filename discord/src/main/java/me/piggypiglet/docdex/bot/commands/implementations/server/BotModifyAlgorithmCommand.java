package me.piggypiglet.docdex.bot.commands.implementations.server;

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
public final class BotModifyAlgorithmCommand extends BotServerCommand {
    private static final String USAGE = "<algorithm>";

    @Inject
    public BotModifyAlgorithmCommand(final @NotNull Set<Server> servers, @NotNull final DatabaseObjects adapters) {
        super(Set.of("algorithm"), USAGE, "Set the server's algorithm.", servers,
                new ModifyAlgorithmCommand(USAGE, adapters));
    }
}
