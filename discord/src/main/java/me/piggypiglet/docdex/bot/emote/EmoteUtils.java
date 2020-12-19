package me.piggypiglet.docdex.bot.emote;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class EmoteUtils {
    private EmoteUtils() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    public static void addEmote(@NotNull final Message message, @NotNull final EmoteWrapper emote) {
        Optional.ofNullable(emote.getEmote())
                .map(message::addReaction)
                .ifPresentOrElse(RestAction::queue, () -> message.addReaction(Objects.requireNonNull(emote.getUnicode())).queue());
    }
}
