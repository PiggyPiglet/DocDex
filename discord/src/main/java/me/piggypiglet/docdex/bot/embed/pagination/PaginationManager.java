package me.piggypiglet.docdex.bot.embed.pagination;

import com.google.inject.Singleton;
import me.piggypiglet.docdex.bot.emote.EmoteWrapper;
import me.piggypiglet.docdex.bot.utils.PermissionUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.TimeUnit;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class PaginationManager {
    private final Map<String, Map<EmoteWrapper, MessageEmbed>> paginatedMessages = ExpiringMap.builder()
            .expiration(15, TimeUnit.MINUTES)
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .build();

    @NotNull
    public Map<String, Map<EmoteWrapper, MessageEmbed>> getPaginatedMessages() {
        return paginatedMessages;
    }

    public void addPaginatedMessage(@NotNull final String id, @NotNull final Map<EmoteWrapper, MessageEmbed> pages) {
        paginatedMessages.put(id, pages);
    }

    public void process(@NotNull final Message message, @NotNull final User user,
                        @NotNull final MessageReaction reaction) {
        final Map<EmoteWrapper, MessageEmbed> pages = paginatedMessages.get(message.getId());

        if (pages == null) {
            return;
        }

        if (message.isFromGuild()) {
            reaction.removeReaction(user).queue(success -> {}, failure -> {
                if (failure instanceof PermissionException) {
                    PermissionUtils.sendPermissionError(message, ((PermissionException) failure).getPermission());
                }
            });
        }

        final MessageReaction.ReactionEmote reactionEmote = reaction.getReactionEmote();
        final EmoteWrapper emote;

        if (reactionEmote.isEmote()) {
            emote = EmoteWrapper.from(reactionEmote.getEmote());
        } else {
            emote = EmoteWrapper.from(reactionEmote.getEmoji());
        }

        final MessageEmbed requestedPage = pages.get(emote);

        if (requestedPage == null) {
            return;
        }

        message.editMessage(requestedPage).queue(success -> {}, failure -> message.getChannel().sendMessage("Something went wrong oof").queue());
    }
}
