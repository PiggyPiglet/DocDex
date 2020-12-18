package me.piggypiglet.docdex.bot.commands;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.bot.commands.implementations.HelpCommand;
import me.piggypiglet.docdex.config.Config;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
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
                             @NotNull final HelpCommand helpCommand) {
        this.prefix = config.getPrefix().toLowerCase();
        this.commands = commands;
        this.unknownCommand = helpCommand;

        commands.add(helpCommand);
    }

    public void process(@NotNull final User user, @NotNull final Message message) {
        final String rawMessage = message.getContentRaw().toLowerCase();

        if (!rawMessage.startsWith(prefix)) {
            return;
        }

        final JDACommand command = commands.stream()
                .filter(possibleCommand -> rawMessage.startsWith(prefix + possibleCommand.getMatch()))
                .findAny().orElse(unknownCommand);

        command.execute(user, message, prefix + command.getMatch());
    }

    @NotNull
    public Set<JDACommand> getCommands() {
        return commands;
    }
}
