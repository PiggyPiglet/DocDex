package me.piggypiglet.docdex.bot.listeners;

import com.google.inject.Inject;
import me.piggypiglet.docdex.db.adapters.DatabaseObjectAdapters;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.server.creation.ServerCreator;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class GuildJoinListener extends ListenerAdapter {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);

    private final Set<Server> servers;
    private final DatabaseObjectAdapters adapters;

    @Inject
    public GuildJoinListener(@NotNull final Set<Server> servers, @NotNull final DatabaseObjectAdapters adapters) {
        this.servers = servers;
        this.adapters = adapters;
    }

    @Override
    public void onGuildJoin(@NotNull final GuildJoinEvent event) {
        EXECUTOR.submit(() -> {
            final Server server = ServerCreator.createInstance(event.getGuild().getId());
            servers.add(server);
            adapters.save(server);
        });
    }
}
