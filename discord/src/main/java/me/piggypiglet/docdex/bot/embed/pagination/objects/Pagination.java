package me.piggypiglet.docdex.bot.embed.pagination.objects;

import com.google.common.collect.Iterables;
import me.piggypiglet.docdex.bot.emote.EmoteUtils;
import me.piggypiglet.docdex.bot.emote.EmoteWrapper;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class Pagination {
    private final Map<EmoteWrapper, MessageEmbed> pages;

    private Pagination(@NotNull @Unmodifiable final Map<EmoteWrapper, MessageEmbed> pages) {
        this.pages = pages;
    }

    @NotNull
    @Unmodifiable
    public Map<EmoteWrapper, MessageEmbed> getPages() {
        return pages;
    }

    @Nullable
    public RestAction<Message> send(@NotNull final MessageChannel channel) {
        if (pages.isEmpty()) {
            return null;
        }

        final MessageEmbed embed = Iterables.get(pages.values(), 0);

        return channel.sendMessage(embed)
                .map(message -> {
                    pages.keySet().forEach(emote -> EmoteUtils.addEmote(message, emote));
                    return message;
                });
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private static final List<EmoteWrapper> DEFAULT_EMOTES = Stream.of(
                "one", "two", "three", "four", "five",
                "six", "seven", "eight", "nine"
        ).map(emote -> ':' + emote + ':').map(EmoteWrapper::from).collect(Collectors.toList());

        private final Map<EmoteWrapper, MessageEmbed> pages = new LinkedHashMap<>();
        private final AtomicInteger index = new AtomicInteger();

        private Builder() {}

        @NotNull
        public Builder page(@NotNull final MessageEmbed page) {
            final int i = index.getAndIncrement();

            if (i > DEFAULT_EMOTES.size() - 1) {
                return this;
            }

            return page(DEFAULT_EMOTES.get(i), page);
        }

        @NotNull
        public Builder page(@NotNull final EmoteWrapper emote, @NotNull final MessageEmbed page) {
            pages.put(emote, page);
            return this;
        }

        @NotNull
        public Builder pages(@NotNull final List<MessageEmbed> pages) {
            pages.forEach(this::page);
            return this;
        }

        @NotNull
        public Builder pages(@NotNull final Map<EmoteWrapper, MessageEmbed> values) {
            pages.putAll(values);
            return this;
        }

        @NotNull
        public Pagination build() {
            return new Pagination(Collections.unmodifiableMap(pages));
        }
    }
}
