package me.piggypiglet.docdex.bot.commands.implementations.server;

import com.google.common.collect.Lists;
import me.piggypiglet.docdex.bot.commands.framework.BotCommand;
import me.piggypiglet.docdex.bot.commands.framework.PermissionCommand;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.db.server.commands.ServerCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class BotServerCommand extends BotCommand implements PermissionCommand {
    private final Set<Server> servers;
    private final ServerCommand command;

    protected BotServerCommand(final @NotNull Set<String> matches, final @NotNull String usage,
                               final @NotNull String description, @NotNull final Set<Server> servers,
                               @NotNull final ServerCommand command) {
        super(matches, usage, description);

        this.servers = servers;
        this.command = command;
    }

    @Override
    protected void execute(final @NotNull User user, final @NotNull Message message,
                           @NotNull final List<String> args) {
        if (args.size() < 1 || args.get(0).isBlank()) {
            command.sendUsage(string -> message.getChannel().sendMessage(string).queue());
            return;
        }

        final Server server;

        if (message.isFromGuild()) {
            server = servers.stream()
                    .filter(element -> element.getId().equals(message.getGuild().getId()))
                    .findAny().orElse(null);
        } else {
            server = null;
        }

        if (server == null) {
            message.getChannel().sendMessage("You are not in a server.").queue();
            return;
        }

        final List<String> newArgs = Lists.newArrayList(message.getGuild().getId());
        newArgs.addAll(args);

        command.run(server, newArgs, string -> message.getChannel().sendMessage(string).queue());
    }
}
