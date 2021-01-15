package me.piggypiglet.docdex.bot.commands.listener;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.bot.listeners.GuildJoinHandler;
import me.piggypiglet.docdex.db.dbo.DatabaseObjects;
import me.piggypiglet.docdex.db.server.Server;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class PrefixResetListener extends ListenerAdapter {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);

    private final Set<Server> servers;
    private final GuildJoinHandler guildJoinHandler;
    private final Server defaultServer;
    private final DatabaseObjects databaseObjects;

    @Inject
    public PrefixResetListener(@NotNull final Set<Server> servers, @NotNull final GuildJoinHandler guildJoinHandler,
                               @NotNull @Named("default") final Server defaultServer, @NotNull final DatabaseObjects databaseObjects) {
        this.servers = servers;
        this.guildJoinHandler = guildJoinHandler;
        this.defaultServer = defaultServer;
        this.databaseObjects = databaseObjects;
    }

    @Override
    public void onGuildMessageReceived(@NotNull final GuildMessageReceivedEvent event) {
        EXECUTOR.submit(() -> {
            if (event.getAuthor().isBot()) {
                return;
            }

            if (!event.getMessage().getContentRaw().startsWith(event.getJDA().getSelfUser().getAsMention().replace("@", "@!") + " resetprefix")) {
                return;
            }

            final Guild guild = event.getGuild();
            final Server server = servers.stream()
                    .filter(element -> element.getId().equals(guild.getId()))
                    .findAny().orElseGet(() -> guildJoinHandler.joinGuild(guild).join());
            final Member member = Objects.requireNonNull(event.getMember());

            if (!member.hasPermission(Permission.ADMINISTRATOR) && member.getRoles().stream()
                    .map(Role::getId)
                    .noneMatch(server.getRoles()::contains)) {
                return;
            }

            server.setPrefix(defaultServer.getPrefix());
            databaseObjects.save(server);
            event.getChannel().sendMessage("Successfully set the prefix to " + server.getPrefix()).queue();
        });
    }
}
