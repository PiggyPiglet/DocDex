package me.piggypiglet.docdex.bot.commands.implementations;

import com.google.common.collect.Lists;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.util.Types;
import me.piggypiglet.docdex.bot.commands.framework.BotCommand;
import me.piggypiglet.docdex.bot.embed.pagination.PaginationManager;
import me.piggypiglet.docdex.bot.embed.pagination.objects.Pagination;
import me.piggypiglet.docdex.bot.embed.utils.EmbedUtils;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.config.Javadoc;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JavadocsCommand extends BotCommand {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
    private static final Type JAVADOC_SET = Types.setOf(Javadoc.class);
    private static final MessageEmbed EMBED = new EmbedBuilder()
            .setAuthor("Javadocs:", null, EmbedUtils.ICON)
            .setColor(EmbedUtils.COLOUR)
            .build();

    private final Config config;
    private final PaginationManager paginationManager;

    @Inject
    public JavadocsCommand(@NotNull final Config config, @NotNull final PaginationManager paginationManager) {
        super(Set.of("javadocs", "docs"), "", "Get a list of javadocs.");
        this.config = config;
        this.paginationManager = paginationManager;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void execute(final @NotNull User user, final @NotNull Message message) {
        final HttpRequest request = HttpRequest.newBuilder(URI.create(config.getUrl() + "/javadocs"))
                .build();
        CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    final List<MessageEmbed> pages = Lists.partition(((Set<Javadoc>) GSON.fromJson(json, JAVADOC_SET)).stream()
                            .map(Javadoc::getNames)
                            .map(names -> "• " + String.join("/", names))
                            .collect(Collectors.toList()), 20)
                            .stream()
                            .map(JavadocsCommand::format)
                            .map(page -> new EmbedBuilder(EMBED).setDescription("```\n" + page + "```").build())
                            .collect(Collectors.toList());
                    final Pagination pagination = Pagination.builder()
                            .pages(pages)
                            .build();

                    Optional.ofNullable(pagination.send(message.getChannel())).ifPresent(action ->
                            action.queue(sentMessage -> paginationManager.addPaginatedMessage(sentMessage.getId(), pagination.getPages())));
                })
                .exceptionally(throwable -> {
                    LOGGER.error("", throwable);
                    return null;
                });
    }

    @NotNull
    private static String format(@NotNull final List<String> strings) {
        if (strings.size() <= 10) {
            return String.join("\n", strings);
        }

        final int length = strings.stream()
                .map(String::length)
                .max(Comparator.comparingInt(i -> i))
                .orElse(7) + 3;
        final StringBuilder builder = new StringBuilder();
        final List<List<String>> partitions = Lists.partition(strings, 10);
        final List<String> first = partitions.get(0);
        final List<String> second = partitions.get(1);

        for (int i = 0; i < 10; ++i) {
            final String left = first.get(i);
            builder.append(left).append(spaces(length - left.length()));

            if (second.size() > i) {
                builder.append(second.get(i));
            }

            builder.append('\n');
        }

        return builder.toString();
    }

    @NotNull
    private static String spaces(final int quantity) {
        return IntStream.rangeClosed(0, quantity).mapToObj(i -> " ").collect(Collectors.joining());
    }
}
