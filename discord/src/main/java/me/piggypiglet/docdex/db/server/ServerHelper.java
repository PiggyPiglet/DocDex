package me.piggypiglet.docdex.db.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.bot.listeners.GuildJoinHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class ServerHelper {
    private final Set<Server> servers;
    private final Server defaultServer;
    private final GuildJoinHandler guildJoinHandler;

    @Inject
    public ServerHelper(@NotNull final Set<Server> servers, @NotNull @Named("default") final Server defaultServer,
                        @NotNull final GuildJoinHandler guildJoinHandler) {
        this.servers = servers;
        this.defaultServer = defaultServer;
        this.guildJoinHandler = guildJoinHandler;
    }

    @NotNull
    public Server getServer(@NotNull final Message message) {
        final Server server;

        if (message.isFromGuild()) {
            final Guild guild = message.getGuild();

            server = servers.stream()
                    .filter(element -> element.getId().equals(guild.getId()))
                    .findAny().orElseGet(() -> guildJoinHandler.joinGuild(guild).join());
        } else {
            server = defaultServer;
        }

        return server;
    }
}
