package me.piggypiglet.docdex.bot.emote;

import me.piggypiglet.docdex.bot.utils.PermissionUtils;
import net.dv8tion.jda.api.entities.Message;
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
        if (emote.getEmote() == null) {
            PermissionUtils.queue(PermissionUtils.create(() -> message.addReaction(Objects.requireNonNull(emote.getUnicode())), message), message);
        } else {
            PermissionUtils.queue(PermissionUtils.create(() -> message.addReaction(emote.getEmote()), message), message);
        }
    }
}
