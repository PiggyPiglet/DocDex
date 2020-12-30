package me.piggypiglet.docdex.bot.commands.implementations;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.bot.commands.framework.BotCommand;
import me.piggypiglet.docdex.bot.embed.utils.EmbedUtils;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.scanning.annotations.Hidden;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Hidden
public final class HelpCommand extends BotCommand {
    private final Set<BotCommand> commands;
    private final Set<Server> servers;
    private final String defaultPrefix;

    @Inject
    public HelpCommand(@NotNull @Named("jda commands") final Set<BotCommand> commands, @NotNull final Set<Server> servers,
                       @NotNull @Named("default") final Server defaultServer) {
        super(Set.of("help"), "", "This page.");
        this.commands = commands;
        this.servers = servers;
        this.defaultPrefix = defaultServer.getPrefix();
    }

    @Override
    public void execute(final @NotNull User user, final @NotNull Message message) {
        final String prefix;

        if (message.isFromGuild()) {
            prefix = servers.stream()
                    .filter(server -> server.getId().equals(message.getGuild().getId()))
                    .findAny()
                    .map(Server::getPrefix)
                    .orElse(defaultPrefix);
        } else {
            prefix = defaultPrefix;
        }

        final EmbedBuilder embed = new EmbedBuilder();
        final Set<String> helpMessages = new TreeSet<>(Comparator.comparingInt(String::length));

        commands.stream().filter(command -> command != this).forEach(command ->
                command.getMatches().forEach(match ->
                        helpMessages.add(
                                "**" +
                                prefix +
                                match +
                                (command.getUsage().isBlank() ? "" : ' ' + command.getUsage()) +
                                ":**\n• " +
                                command.getDescription()
                        )
                )
        );

        embed.setDescription(String.join("\n", helpMessages));
        embed.setColor(EmbedUtils.COLOUR);
        embed.setAuthor("Help:", null, EmbedUtils.ICON);
        message.getChannel().sendMessage(embed.build()).queue();
    }
}
