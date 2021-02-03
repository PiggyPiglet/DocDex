package me.piggypiglet.docdex.db.server.commands;

import me.piggypiglet.docdex.db.dbo.DatabaseObjects;
import me.piggypiglet.docdex.db.server.Server;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public final class ModifyDefaultJavadocCommand extends ServerCommand {
    public ModifyDefaultJavadocCommand(@NotNull final String usage, @NotNull final DatabaseObjects adapters) {
        super(usage, 2, adapters);
    }

    @Override
    protected boolean execute(@NotNull final Server server, @NotNull final List<String> args,
                              @NotNull final Consumer<String> messageFunction) {
        server.setDefaultJavadoc(args.get(1));
        messageFunction.accept("Successfully set " + server.getId() + "'s default javadoc to " + args.get(1));
        return true;
    }
}
