package me.piggypiglet.docdex.bot.embed.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class EmbedUtils {
    public static final int DEPRECATED_COLOUR = 14561585; // #DE3131
    public static final int COLOUR = 199061147;
    public static final String ICON = "https://cdn.piggypiglet.me/docdex/avatar.png";

    private static final Set<EmbedFunction<?>> EMBED_FUNCTIONS = Set.of(
            function(MessageEmbed::getAuthor, (builder, author) -> builder.setAuthor(author.getName(), author.getUrl(), author.getIconUrl())),
            function(MessageEmbed::getColor, EmbedBuilder::setColor),
            function(MessageEmbed::getColorRaw, EmbedBuilder::setColor),
            function(MessageEmbed::getFields, (builder, fields) -> fields.forEach(builder::addField)),
            function(MessageEmbed::getFooter, (builder, footer) -> builder.setFooter(footer.getText(), footer.getIconUrl())),
            function(MessageEmbed::getImage, (builder, image) -> builder.setImage(image.getUrl())),
            function(MessageEmbed::getThumbnail, (builder, thumbnail) -> builder.setThumbnail(thumbnail.getUrl())),
            function(MessageEmbed::getTimestamp, EmbedBuilder::setTimestamp),
            function(embed -> Map.of(
                    "title", Optional.ofNullable(embed.getTitle()).orElse(""),
                    "url", Optional.ofNullable(embed.getUrl()).orElse("")
            ), (builder, map) -> builder.setTitle(map.getOrDefault("title", null), map.getOrDefault("url", null)))
    );

    private EmbedUtils() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    public static void merge(@NotNull final EmbedBuilder from, @NotNull final EmbedBuilder to) {
        final MessageEmbed mainEmbed = from.build();
        EMBED_FUNCTIONS.forEach(functions -> applyFunctions(mainEmbed, to, functions));
    }

    private static <T> void applyFunctions(@NotNull final MessageEmbed from, @NotNull final EmbedBuilder to,
                                           @NotNull final EmbedFunction<T> functions) {
        final T value = functions.getGetter().apply(from);

        if (value != null) {
            functions.getSetter().accept(to, value);
        }
    }

    @NotNull
    private static <T> EmbedFunction<T> function(@NotNull final Function<MessageEmbed, T> getter, @NotNull final BiConsumer<EmbedBuilder, T> setter) {
        return new EmbedFunction<>(getter, setter);
    }

    private static final class EmbedFunction<T> {
        private final Function<MessageEmbed, T> getter;
        private final BiConsumer<EmbedBuilder, T> setter;

        private EmbedFunction(@NotNull final Function<MessageEmbed, T> getter, @NotNull final BiConsumer<EmbedBuilder, T> setter) {
            this.getter = getter;
            this.setter = setter;
        }

        @NotNull
        public Function<MessageEmbed, T> getGetter() {
            return getter;
        }

        @NotNull
        public BiConsumer<EmbedBuilder, T> getSetter() {
            return setter;
        }
    }
}
