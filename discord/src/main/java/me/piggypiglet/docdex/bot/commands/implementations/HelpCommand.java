package me.piggypiglet.docdex.bot.commands.implementations;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.bot.commands.JDACommand;
import me.piggypiglet.docdex.bot.commands.JDACommandHandler;
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

    @Inject
    public HelpCommand(@NotNull @Named("jda commands") final Set<JDACommand> commands) {
        super("help", "this page.");
        this.commands = commands;
    }

    @Override
    public void execute(final @NotNull User user, final @NotNull Message message) {
        final StringBuilder helpMessage = new StringBuilder();

        commands.forEach(command -> helpMessage.append("\n").append(command.getMatch()).append(" - ").append(command.getDescription()));

        message.getChannel().sendMessage(helpMessage.toString()).queue();
    }
}
