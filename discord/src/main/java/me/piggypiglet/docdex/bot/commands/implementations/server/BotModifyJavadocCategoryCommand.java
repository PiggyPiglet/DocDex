package me.piggypiglet.docdex.bot.commands.implementations.server;

import com.google.inject.Inject;
import me.piggypiglet.docdex.db.dbo.DatabaseObjects;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.server.commands.ModifyJavadocCategoryCommand;
import me.piggypiglet.docdex.scanning.annotations.Hidden;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Hidden
public final class BotModifyJavadocCategoryCommand extends BotServerCommand {
    private static final String USAGE = "<javadocs/description> [add/remove] <category name> <value>";

    @Inject
    public BotModifyJavadocCategoryCommand(@NotNull final Set<Server> servers, @NotNull final DatabaseObjects adapters) {
        super(Set.of("command"), USAGE, "Edit javadoc categories.", servers,
                new ModifyJavadocCategoryCommand(USAGE, adapters));
    }
}
