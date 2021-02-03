package me.piggypiglet.docdex.bot.embed.pagination.objects;

import com.google.common.collect.Iterables;
import me.piggypiglet.docdex.bot.emote.EmoteUtils;
import me.piggypiglet.docdex.bot.emote.EmoteWrapper;
import me.piggypiglet.docdex.bot.utils.PermissionUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
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
    private final String author;

    private Pagination(@NotNull @Unmodifiable final Map<EmoteWrapper, MessageEmbed> pages, @NotNull final String author) {
        this.pages = pages;
        this.author = author;
    }

    @NotNull
    @Unmodifiable
    public Map<EmoteWrapper, MessageEmbed> getPages() {
        return pages;
    }

    @NotNull
    public String getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return "Pagination{" +
                "pages=" + pages.size() +
                ", author='" + author + '\'' +
                '}';
    }

    @Nullable
    public RestAction<Message> send(@NotNull final Message message) {
        if (pages.isEmpty()) {
            return null;
        }

        final MessageEmbed embed = Iterables.get(pages.values(), 0);

        try {
            return message.getChannel().sendMessage(embed)
                    .map(potentialMessage -> {
                        pages.keySet().forEach(emote -> EmoteUtils.addEmote(potentialMessage, emote));
                        return potentialMessage;
                    });
        } catch (InsufficientPermissionException exception) {
            PermissionUtils.sendPermissionError(message, exception.getPermission());
            return null;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private static final List<EmoteWrapper> DEFAULT_EMOTES = Stream.of(
                "zero", "one", "two", "three", "four", "five",
                "six", "seven", "eight", "nine", "keycap_ten"
        ).map(emote -> ':' + emote + ':').map(EmoteWrapper::from).collect(Collectors.toList());

        private final Map<EmoteWrapper, MessageEmbed> pages = new LinkedHashMap<>();
        private final AtomicInteger index = new AtomicInteger();
        private String author = "";

        private Builder() {}

        @NotNull
        public Builder page(@NotNull final MessageEmbed page) {
            final int i = index.getAndIncrement();

            if (i > DEFAULT_EMOTES.size() - 1) {
                return this;
            }

            final EmbedBuilder builder = new EmbedBuilder(page);
            final String pageNum = "Page " + i;

            final MessageEmbed.Footer footer = page.getFooter();

            if (footer != null) {
                builder.setFooter(pageNum + " • " + footer.getText(), footer.getIconUrl());
            } else {
                builder.setFooter(pageNum);
            }

            return page(DEFAULT_EMOTES.get(i), builder.build());
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
        public Builder author(@NotNull final String value) {
            author = value;
            return this;
        }

        @NotNull
        public Pagination build() {
            return new Pagination(Collections.unmodifiableMap(pages), author);
        }
    }
}
