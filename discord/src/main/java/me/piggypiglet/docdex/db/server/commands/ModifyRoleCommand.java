package me.piggypiglet.docdex.db.server.commands;

import me.piggypiglet.docdex.db.dbo.DatabaseObjects;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.server.commands.util.ModificationOptions;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ModifyRoleCommand extends ServerCommand {
    public ModifyRoleCommand(@NotNull final String usage, @NotNull final DatabaseObjects adapters) {
        super(usage, 3, adapters);
    }

    @Override
    protected void execute(final @NotNull Server server, final @NotNull List<String> args,
                           @NotNull final Consumer<String> messageFunction) {
        final ModificationOptions modificationOption = ModificationOptions.MAP.get(args.get(1));

        if (modificationOption == null) {
            sendUsage(messageFunction);
            return;
        }

        final Set<String> roles = server.getRoles();
        final String role = args.get(2);

        switch (modificationOption) {
            case ADD:
                roles.add(role);
                break;

            case REMOVE:
                roles.remove(role);
                break;
        }

        messageFunction.accept("Successfully " + modificationOption + " to " + server.getId() + "'s role list.");
    }
}
