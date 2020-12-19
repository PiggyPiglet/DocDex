package me.piggypiglet.docdex.bot.embed.pagination.listener;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bot.embed.pagination.PaginationManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class PaginationListener extends ListenerAdapter {
    private final Set<String> ids;
    private final PaginationManager paginationManager;

    @Inject
    public PaginationListener(@NotNull final PaginationManager paginationManager) {
        this.ids = paginationManager.getPaginatedMessages().keySet();
        this.paginationManager = paginationManager;
    }

    @Override
    public void onMessageReactionAdd(@NotNull final MessageReactionAddEvent event) {
        if (Optional.ofNullable(event.getUser()).map(User::isBot).orElse(true)) {
            return;
        }

        final String id = event.getMessageId();

        event.getChannel().retrieveMessageById(id).queue(message -> {
            if (!ids.contains(id)) {
                return;
            }

            paginationManager.process(message, event.getUser(), event.getReaction());
        });
    }

    @Override
    public void onMessageDelete(@NotNull final MessageDeleteEvent event) {
        ids.remove(event.getMessageId());
    }
}
