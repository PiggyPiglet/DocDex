package me.piggypiglet.docdex.bot.commands.implementations;

import me.piggypiglet.docdex.bot.commands.JDACommand;
import me.piggypiglet.docdex.scanning.annotations.Hidden;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Hidden
public final class HelpCommand extends JDACommand {


    public HelpCommand() {
        super("help", "this page.");
    }

    @Override
    public void execute(final @NotNull User user, final @NotNull Message message) {

    }
}
