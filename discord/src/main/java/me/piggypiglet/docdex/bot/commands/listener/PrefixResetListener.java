package me.piggypiglet.docdex.bot.commands.listener;

import com.google.inject.Inject;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.db.dbo.DatabaseObjects;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.server.ServerHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class PrefixResetListener extends ListenerAdapter {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);

    private final ServerHelper serverHelper;
    private final Config config;
    private final DatabaseObjects adapters;

    @Inject
    public PrefixResetListener(@NotNull final ServerHelper serverHelper, @NotNull final Config config,
                               @NotNull final DatabaseObjects adapters) {
        this.serverHelper = serverHelper;
        this.config = config;
        this.adapters = adapters;
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

            final Server server = serverHelper.getServer(event.getMessage());
            final Member member = Objects.requireNonNull(event.getMember());

            if (!member.hasPermission(Permission.ADMINISTRATOR) && member.getRoles().stream()
                    .map(Role::getId)
                    .noneMatch(server.getRoles()::contains)) {
                return;
            }

            server.setPrefix(config.getPrefix());
            adapters.save(server);
            event.getChannel().sendMessage("Successfully set the prefix to " + server.getPrefix()).queue();
        });
    }
}
