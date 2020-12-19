package me.piggypiglet.docdex.bot.commands.implementations;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.bot.commands.JDACommand;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.scanning.annotations.Hidden;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Hidden
public final class HelpCommand extends JDACommand {
    private final Set<JDACommand> commands;
    private final String prefix;

    @Inject
    public HelpCommand(@NotNull @Named("jda commands") final Set<JDACommand> commands, @NotNull final Config config) {
        super(Set.of("help"), "this page.");
        this.commands = commands;
        this.prefix = config.getPrefix();
    }

    @Override
    public void execute(final @NotNull User user, final @NotNull Message message) {
        final StringBuilder helpMessage = new StringBuilder();

        commands.forEach(command ->
                command.getMatches().forEach(match ->
                        helpMessage
                                .append(prefix)
                                .append(match)
                                .append(" - ")
                                .append(command.getDescription())
                                .append('\n')
                )
        );

        message.getChannel().sendMessage(helpMessage.toString()).queue();
    }
}
