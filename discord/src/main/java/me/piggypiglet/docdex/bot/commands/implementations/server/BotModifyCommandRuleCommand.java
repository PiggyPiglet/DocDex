package me.piggypiglet.docdex.bot.commands.implementations.server;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.db.dbo.DatabaseObjects;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.server.commands.ModifyCommandRuleCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class BotModifyCommandRuleCommand extends BotServerCommand {
    private static final String USAGE = "<allow/disallow/recommendation> [add/remove] <command> <value>";

    @Inject
    public BotModifyCommandRuleCommand(@NotNull @Named("default") final Server defaultServer, @NotNull final DatabaseObjects adapters) {
        super(Set.of("command"), USAGE, "Edit rules for a command.", defaultServer,
                new ModifyCommandRuleCommand(USAGE, adapters));
    }
}
