package me.piggypiglet.docdex.bot.commands;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.bot.commands.framework.BotCommand;
import me.piggypiglet.docdex.bot.commands.framework.PermissionCommand;
import me.piggypiglet.docdex.bot.commands.implementations.HelpCommand;
import me.piggypiglet.docdex.bot.commands.implementations.documentation.SimpleCommand;
import me.piggypiglet.docdex.bot.emote.EmoteUtils;
import me.piggypiglet.docdex.bot.emote.EmoteWrapper;
import me.piggypiglet.docdex.db.server.CommandRule;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.server.ServerHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static me.piggypiglet.docdex.bot.utils.PermissionUtils.create;
import static me.piggypiglet.docdex.bot.utils.PermissionUtils.queue;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class BotCommandHandler {
    private static final EmoteWrapper TRASH = EmoteWrapper.from("\uD83D\uDDD1️");

    private final ServerHelper serverHelper;
    private final Set<BotCommand> commands;
    private final BotCommand unknownCommand;
    private final CommandRule defaultCommandRule;

    private final Map<String, Map.Entry<String, String>> currentCommands = ExpiringMap.builder()
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .expiration(15, TimeUnit.MINUTES)
            .build();

    @Inject
    public BotCommandHandler(@NotNull final ServerHelper serverHelper, @NotNull @Named("jda commands") final Set<BotCommand> commands,
                             @NotNull final SimpleCommand defaultCommand, @NotNull final HelpCommand helpCommand,
                             @NotNull @Named("default") final CommandRule defaultCommandRule) {
        this.serverHelper = serverHelper;
        this.commands = commands;
        this.unknownCommand = defaultCommand;
        this.defaultCommandRule = defaultCommandRule;

        commands.add(defaultCommand);
        commands.add(helpCommand);
    }

    public void processCommand(@NotNull final User user, @NotNull final Message message) {
        final Server server = serverHelper.getServer(message);
        final String strippedMessage = message.getContentStripped().toLowerCase();

        if (!strippedMessage.startsWith(server.getPrefix())) {
            return;
        }

        final StringBuilder prefixBuilder = new StringBuilder(server.getPrefix().toLowerCase());

        for (final char character : strippedMessage.substring(server.getPrefix().length()).toCharArray()) {
            if (character != ' ') {
                break;
            }

            prefixBuilder.append(' ');
        }

        final String prefix = prefixBuilder.toString();

        final AtomicReference<String> match = new AtomicReference<>("");

        final BotCommand command = commands.stream()
                .filter(possibleCommand -> possibleCommand.getMatches().stream().anyMatch(possibleMatch -> {
                    final boolean matches = strippedMessage.startsWith(prefix + possibleMatch);

                    if (matches) {
                        match.set(possibleMatch);
                    }

                    return matches;
                }))
                .findAny().orElse(unknownCommand);

        if (command instanceof PermissionCommand) {
            if (!message.isFromGuild()) {
                sendAndDelete(message.getChannel().sendMessage("You cannot use this command outside of a server."));
                return;
            }

            final Member member = message.getGuild().retrieveMemberById(user.getId()).complete();

            if (!member.hasPermission(Permission.ADMINISTRATOR) && member.getRoles().stream()
                    .map(Role::getId)
                    .noneMatch(server.getRoles()::contains)) {
                message.delete().queue();
                sendAndDelete(message.getChannel().sendMessage("You do not have permission to use this command."));
                return;
            }
        }

        final CommandRule rule = server.getRules().getOrDefault(match.get(), defaultCommandRule);

        final Set<String> allowed = rule.getAllowed();
        final Set<String> disallowed = rule.getDisallowed();
        final String channel = message.getChannel().getId();

        if (!(allowed.isEmpty() || allowed.contains(channel)) || (!disallowed.isEmpty() && disallowed.contains(channel))) {
            message.delete().queue();
            sendAndDelete(message.getChannel().sendMessage(rule.getRecommendation()));
            return;
        }

        final int start = (prefix + match.get()).length();

        Optional.ofNullable(command.run(user, message, server, start)).ifPresentOrElse(action -> queue(action, success -> {
            if (message.isFromGuild()) {
                currentCommands.put(success.getId(), Map.entry(message.getId(), user.getId()));
                EmoteUtils.addEmote(success, TRASH);
            }
        }, message), () -> queue(create(message::delete, message), message));
    }

    public void processCommandDeletion(@NotNull final GuildMessageReactionAddEvent event) {
        final TextChannel channel = event.getChannel();
        final String id = event.getMessageId();

        channel.retrieveMessageById(id).queue(message -> {
            final MessageReaction.ReactionEmote reaction = event.getReactionEmote();
            final Map.Entry<String, String> entry = currentCommands.get(id);

            if (entry == null) {
                return;
            }

            queue(create(() -> event.getReaction().removeReaction(event.getUser()), message), message);

            final String commandRequestId = entry.getKey();
            final String authorId = entry.getValue();

            if (authorId.equals(event.getUserId()) && reaction.isEmoji() && reaction.getEmoji().equals(TRASH.getUnicode())) {
                queue(create(message::delete, message), message);
                queue(
                        create(() -> channel.retrieveMessageById(commandRequestId), message), // get message
                        success -> queue(create(success::delete, message), message), // delete message
                        message
                );
                currentCommands.remove(id);
            }
        }, failure -> currentCommands.remove(id));
    }


    @NotNull
    public Set<BotCommand> getCommands() {
        return commands;
    }

    private static void sendAndDelete(@NotNull final MessageAction action) {
        action.queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS, success -> {}, failure -> {}));
    }
}
