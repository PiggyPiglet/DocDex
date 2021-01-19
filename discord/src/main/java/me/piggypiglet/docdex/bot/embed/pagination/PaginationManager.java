package me.piggypiglet.docdex.bot.embed.pagination;

import com.google.inject.Singleton;
import me.piggypiglet.docdex.bot.embed.pagination.objects.Pagination;
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
    private final Map<String, Pagination> paginatedMessages = ExpiringMap.builder()
            .expiration(15, TimeUnit.MINUTES)
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .build();

    @NotNull
    public Map<String, Pagination> getPaginatedMessages() {
        return paginatedMessages;
    }

    public void addPaginatedMessage(@NotNull final String id, @NotNull final Pagination pagination) {
        paginatedMessages.put(id, pagination);
    }

    public void process(@NotNull final Message message, @NotNull final User user,
                        @NotNull final MessageReaction reaction) {
        final Pagination pagination = paginatedMessages.get(message.getId());

        if (pagination == null) {
            return;
        }

        final boolean isNotAuthor = !message.getAuthor().getId().equals(user.getId());

        if (message.isFromGuild() || isNotAuthor) {
            try {
                reaction.removeReaction(user).queue();
            } catch (PermissionException exception) {
                PermissionUtils.sendPermissionError(message, exception.getPermission());
            }
        }

        if (isNotAuthor) {
            return;
        }

        final MessageReaction.ReactionEmote reactionEmote = reaction.getReactionEmote();
        final EmoteWrapper emote;

        if (reactionEmote.isEmote()) {
            emote = EmoteWrapper.from(reactionEmote.getEmote());
        } else {
            emote = EmoteWrapper.from(reactionEmote.getEmoji());
        }

        final MessageEmbed requestedPage = pagination.getPages().get(emote);

        if (requestedPage == null) {
            return;
        }

        message.editMessage(requestedPage).queue(success -> {}, failure -> message.getChannel().sendMessage("Something went wrong oof").queue());
    }
}
