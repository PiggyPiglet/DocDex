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
        try {
            if (emote.getEmote() == null) {
                message.addReaction(Objects.requireNonNull(emote.getUnicode())).queue();
            } else {
                message.addReaction(emote.getEmote()).queue();
            }
        } catch (PermissionException exception) {
            PermissionUtils.sendPermissionError(message, exception.getPermission());
        }
    }
}
