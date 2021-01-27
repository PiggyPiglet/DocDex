package me.piggypiglet.docdex.bot.emote;

import me.piggypiglet.docdex.bot.utils.PermissionUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.PermissionException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class EmoteUtils {
    private EmoteUtils() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    public static void addEmote(@NotNull final Message message, @NotNull final EmoteWrapper emote) {
        // JDA is inconsistent in permission checking, sometimes it'll be in the queue, sometimes it'll be in the action
        try {
            if (emote.getEmote() == null) {
                message.addReaction(Objects.requireNonNull(emote.getUnicode()))
                        .queue(success -> {}, failure -> handleFailure(message, failure));
            } else {
                message.addReaction(emote.getEmote())
                        .queue(success -> {}, failure -> handleFailure(message, failure));
            }
        } catch (PermissionException exception) {
            PermissionUtils.sendPermissionError(message, exception.getPermission());
        }
    }

    private static void handleFailure(@NotNull final Message message, @NotNull final Throwable throwable) {
        if (throwable instanceof PermissionException) {
            PermissionUtils.sendPermissionError(message, ((PermissionException) throwable).getPermission());
        }
    }
}
