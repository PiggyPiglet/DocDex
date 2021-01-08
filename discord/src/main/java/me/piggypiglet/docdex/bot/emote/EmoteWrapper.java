package me.piggypiglet.docdex.bot.emote;

import emoji4j.EmojiUtils;
import net.dv8tion.jda.api.entities.Emote;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class EmoteWrapper {
    private final String unicode;
    private final Emote emote;

    private EmoteWrapper(@Nullable final String unicode, @Nullable final Emote emote) {
        this.unicode = unicode;
        this.emote = emote;
    }

    @NotNull
    public static EmoteWrapper from(@NotNull final String emote) {
        return new EmoteWrapper(EmojiUtils.getEmoji(emote).getEmoji(), null);
    }

    @NotNull
    public static EmoteWrapper from(@NotNull final Emote emote) {
        return new EmoteWrapper(null, emote);
    }

    @Nullable
    String getUnicode() {
        return unicode;
    }

    @Nullable
    Emote getEmote() {
        return emote;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EmoteWrapper that = (EmoteWrapper) o;
        return Objects.equals(unicode, that.unicode) && Objects.equals(emote, that.emote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unicode, emote);
    }
}
