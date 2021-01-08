package me.piggypiglet.docdex.bot.commands;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.bot.commands.framework.BotCommand;
import me.piggypiglet.docdex.bot.commands.framework.PermissionCommand;
import me.piggypiglet.docdex.bot.commands.implementations.HelpCommand;
import me.piggypiglet.docdex.bot.commands.implementations.documentation.SimpleCommand;
import me.piggypiglet.docdex.bot.utils.PermissionUtils;
import me.piggypiglet.docdex.db.server.CommandRule;
import me.piggypiglet.docdex.db.server.Server;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.PermissionException;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class BotCommandHandler {

    private final Set<Server> servers;
    private final Set<BotCommand> commands;
    private final BotCommand unknownCommand;
    private final Server defaultServer;
    private final CommandRule defaultCommandRule;

    @Inject
    public BotCommandHandler(@NotNull final Set<Server> servers, @NotNull @Named("jda commands") final Set<BotCommand> commands,
                             @NotNull final SimpleCommand defaultCommand, @NotNull final HelpCommand helpCommand,
                             @NotNull @Named("default") final Server defaultServer, @NotNull @Named("default") final CommandRule defaultCommandRule) {
        this.servers = servers;
        this.commands = commands;
        this.unknownCommand = defaultCommand;
        this.defaultServer = defaultServer;
        this.defaultCommandRule = defaultCommandRule;

        commands.add(defaultCommand);
        commands.add(helpCommand);
    }

    public void process(@NotNull final User user, @NotNull final Message message) {
        final Server server;

        if (message.isFromGuild()) {
            server = servers.stream()
                    .filter(element -> element.getId().equals(message.getGuild().getId()))
                    .findAny().orElse(defaultServer);
        } else {
            server = defaultServer;
        }

        final String prefix = server.getPrefix().toLowerCase();
        final String rawMessage = message.getContentRaw().toLowerCase();

        if (!rawMessage.startsWith(prefix)) {
            return;
        }

        final AtomicReference<String> match = new AtomicReference<>("");

        final BotCommand command = commands.stream()
                .filter(possibleCommand -> possibleCommand.getMatches().stream().anyMatch(possibleMatch -> {
                    final boolean matches = rawMessage.startsWith(prefix + possibleMatch);

                    if (matches) {
                        match.set(possibleMatch);
                    }

                    return matches;
                }))
                .findAny().orElse(unknownCommand);

        if (command instanceof PermissionCommand) {
            if (!message.isFromGuild()) {
                message.getChannel().sendMessage("You cannot use this command outside of a server.")
                        .queue(sentMessage -> sentMessage.delete().queueAfter(15, TimeUnit.SECONDS));
                return;
            }

            final Member member = message.getGuild().retrieveMemberById(user.getId()).complete();

            if (!member.hasPermission(Permission.ADMINISTRATOR) && member.getRoles().stream()
                    .map(Role::getId)
                    .noneMatch(server.getRoles()::contains)) {
                message.delete().queue();
                message.getChannel().sendMessage("You do not have permission to use this command.")
                        .queue(sentMessage -> sentMessage.delete().queueAfter(15, TimeUnit.SECONDS));
                return;
            }
        }

        final CommandRule rule = server.getRules().getOrDefault(match.get(), defaultCommandRule);

        final Set<String> allowed = rule.getAllowed();
        final Set<String> disallowed = rule.getDisallowed();
        final String channel = message.getChannel().getId();

        if (!(allowed.isEmpty() || allowed.contains(channel)) || (!disallowed.isEmpty() && disallowed.contains(channel))) {
            message.delete().queue();
            message.getChannel().sendMessage(rule.getRecommendation())
                    .queue(sentMessage -> sentMessage.delete().queueAfter(15, TimeUnit.SECONDS));
            return;
        }

        final int start = (prefix + match.get()).length();

        try {
            command.run(user, message, start);
        } catch (PermissionException exception) {
            PermissionUtils.sendPermissionError(message, exception.getPermission());
        }
    }

    @NotNull
    public Set<BotCommand> getCommands() {
        return commands;
    }
}
