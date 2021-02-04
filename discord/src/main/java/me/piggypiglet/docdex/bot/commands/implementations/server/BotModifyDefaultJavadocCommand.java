package me.piggypiglet.docdex.bot.commands.implementations.server;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.db.dbo.DatabaseObjects;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.server.commands.ModifyDefaultJavadocCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class BotModifyDefaultJavadocCommand extends BotServerCommand {
    private static final String USAGE = "<javadoc>";

    @Inject
    public BotModifyDefaultJavadocCommand(@NotNull @Named("default") final Server defaultServer, @NotNull final DatabaseObjects adapters) {
        super(Set.of("default_javadoc"), USAGE, "Set the server's default javadoc.", defaultServer,
                new ModifyDefaultJavadocCommand(USAGE, adapters));
    }
}
