package me.piggypiglet.docdex.bot.commands.listener;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bot.commands.JDACommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JDACommandListener extends ListenerAdapter {
    private final JDACommandHandler commandHandler;

    @Inject
    public JDACommandListener(@NotNull final JDACommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void onMessageReceived(@NotNull final MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            try {
                commandHandler.process(event.getAuthor(), event.getMessage());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
