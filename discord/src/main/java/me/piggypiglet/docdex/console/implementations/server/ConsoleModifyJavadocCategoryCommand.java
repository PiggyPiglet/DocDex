package me.piggypiglet.docdex.console.implementations.server;

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
public final class ConsoleModifyJavadocCategoryCommand extends ConsoleServerCommand {
    @Inject
    public ConsoleModifyJavadocCategoryCommand(@NotNull final Set<Server> servers, @NotNull final DatabaseObjects adapters) {
        super("command", "Edit javadoc categories for a server.", servers,
                new ModifyJavadocCategoryCommand("<server> <javadocs/description> [add/remove] <category name> <value>", adapters));
    }
}
