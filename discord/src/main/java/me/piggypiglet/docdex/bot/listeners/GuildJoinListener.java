package me.piggypiglet.docdex.bot.listeners;

import com.google.inject.Inject;
import me.piggypiglet.docdex.db.dbo.DatabaseObjects;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.server.creation.ServerCreator;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class GuildJoinListener extends ListenerAdapter {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);
    private static final Logger LOGGER = LoggerFactory.getLogger("Bot");

    private final ServerCreator creator;
    private final Set<Server> servers;
    private final DatabaseObjects adapters;

    @Inject
    public GuildJoinListener(@NotNull final ServerCreator creator, @NotNull final Set<Server> servers,
                             @NotNull final DatabaseObjects adapters) {
        this.creator = creator;
        this.servers = servers;
        this.adapters = adapters;
    }

    @Override
    public void onGuildJoin(@NotNull final GuildJoinEvent event) {
        EXECUTOR.submit(() -> {
            final Server server = creator.createInstance(event.getGuild().getId());
            servers.add(server);
            adapters.save(server);

            LOGGER.info("Joined " + event.getGuild().getName());
        });
    }
}
