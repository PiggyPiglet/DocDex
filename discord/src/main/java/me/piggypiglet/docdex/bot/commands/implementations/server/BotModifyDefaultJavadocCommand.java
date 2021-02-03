package me.piggypiglet.docdex.bot.commands.implementations.server;

import com.google.inject.Inject;
import me.piggypiglet.docdex.db.dbo.DatabaseObjects;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.server.commands.ModifyDefaultJavadocCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class BotModifyDefaultJavadocCommand extends BotServerCommand {
    private static final String USAGE = "<javadoc>";

    @Inject
    public BotModifyDefaultJavadocCommand(@NotNull final Set<Server> servers, @NotNull final DatabaseObjects adapters) {
        super(Set.of("javadoc"), USAGE, "Set the server's default javadoc.", servers,
                new ModifyDefaultJavadocCommand(USAGE, adapters));
    }
}
