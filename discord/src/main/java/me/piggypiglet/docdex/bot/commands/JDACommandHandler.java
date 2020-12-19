package me.piggypiglet.docdex.bot.commands;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.bot.commands.implementations.HelpCommand;
import me.piggypiglet.docdex.bot.commands.implementations.documentation.SimpleCommand;
import me.piggypiglet.docdex.config.Config;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class JDACommandHandler {
    private final String prefix;
    private final Set<JDACommand> commands;
    private final JDACommand unknownCommand;

    @Inject
    public JDACommandHandler(@NotNull final Config config, @NotNull @Named("jda commands") final Set<JDACommand> commands,
                             @NotNull final SimpleCommand defaultCommand, @NotNull final HelpCommand helpCommand) {
        this.prefix = config.getPrefix().toLowerCase();
        this.commands = commands;
        this.unknownCommand = defaultCommand;

        commands.add(defaultCommand);
        commands.add(helpCommand);
    }

    public void process(@NotNull final User user, @NotNull final Message message) {
        final String rawMessage = message.getContentRaw().toLowerCase();

        if (!rawMessage.startsWith(prefix)) {
            return;
        }

        final JDACommand command = commands.stream()
                .filter(possibleCommand -> possibleCommand.getMatches().stream().anyMatch(match -> rawMessage.startsWith(prefix + match)))
                .findAny().orElse(unknownCommand);
        final String start = prefix + command.getMatches().stream()
                .filter(match -> rawMessage.startsWith(prefix + match))
                .findAny().orElse("");

        command.run(user, message, start);
    }

    @NotNull
    public Set<JDACommand> getCommands() {
        return commands;
    }
}
