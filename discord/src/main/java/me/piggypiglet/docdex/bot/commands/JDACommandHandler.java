package me.piggypiglet.docdex.bot.commands;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.bot.commands.implementations.HelpCommand;
import me.piggypiglet.docdex.bot.commands.implementations.documentation.SimpleCommand;
import me.piggypiglet.docdex.config.CommandRule;
import me.piggypiglet.docdex.config.Config;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class JDACommandHandler {
    private final Config config;
    private final Set<JDACommand> commands;
    private final JDACommand unknownCommand;

    @Inject
    public JDACommandHandler(@NotNull final Config config, @NotNull @Named("jda commands") final Set<JDACommand> commands,
                             @NotNull final SimpleCommand defaultCommand, @NotNull final HelpCommand helpCommand) {
        this.config = config;
        this.commands = commands;
        this.unknownCommand = defaultCommand;

        commands.add(defaultCommand);
        commands.add(helpCommand);
    }

    public void process(@NotNull final User user, @NotNull final Message message) {
        final String prefix = config.getPrefix().toLowerCase();
        final String rawMessage = message.getContentRaw().toLowerCase();

        if (!rawMessage.startsWith(prefix)) {
            return;
        }

        message.delete().queue();
        final AtomicReference<String> match = new AtomicReference<>("");

        final JDACommand command = commands.stream()
                .filter(possibleCommand -> possibleCommand.getMatches().stream().anyMatch(possibleMatch -> {
                    final boolean matches = rawMessage.startsWith(prefix + possibleMatch);

                    if (matches) {
                        match.set(possibleMatch);
                    }

                    return matches;
                }))
                .findAny().orElse(unknownCommand);
        final CommandRule rule = config.getCommands().get(match.get());

        if (rule != null) {
            final Set<String> allowed = rule.getAllowed();
            final Set<String> disallowed = rule.getDisallowed();
            final String id = message.getChannel().getId();

            if (!(allowed.isEmpty() || allowed.contains(id)) || (!disallowed.isEmpty() && disallowed.contains(id))) {
                message.getChannel().sendMessage(rule.getRecommendation()).queue(sentMessage -> sentMessage.delete().queueAfter(15, TimeUnit.SECONDS));
                return;
            }
        }

        final String start = prefix + match.get();

        command.run(user, message, start);
    }

    @NotNull
    public Set<JDACommand> getCommands() {
        return commands;
    }
}
