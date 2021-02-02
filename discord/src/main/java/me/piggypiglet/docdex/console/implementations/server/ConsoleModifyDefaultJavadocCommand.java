package me.piggypiglet.docdex.console.implementations.server;

import com.google.inject.Inject;
import me.piggypiglet.docdex.db.dbo.DatabaseObjects;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.server.commands.ModifyDefaultJavadocCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class ConsoleModifyDefaultJavadocCommand extends ConsoleServerCommand {
    @Inject
    public ConsoleModifyDefaultJavadocCommand(@NotNull final Set<Server> servers, @NotNull final DatabaseObjects adapters) {
        super("javadoc", "Set a server's default javadoc.", servers,
                new ModifyDefaultJavadocCommand("<server> <javadoc>", adapters));
    }
}
