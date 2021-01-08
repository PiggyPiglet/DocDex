package me.piggypiglet.docdex.bot.commands.listener;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bot.commands.BotCommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class BotCommandListener extends ListenerAdapter {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);
    private static final Logger LOGGER = LoggerFactory.getLogger("JDA Commands");

    private final BotCommandHandler commandHandler;

    @Inject
    public BotCommandListener(@NotNull final BotCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void onMessageReceived(@NotNull final MessageReceivedEvent event) {
        EXECUTOR.submit(() -> {
            if (!event.getAuthor().isBot()) {
                try {
                    commandHandler.process(event.getAuthor(), event.getMessage());
                } catch (Exception exception) {
                    LOGGER.error("Something went wrong when running a command", exception);
                }
            }
        });
    }
}
