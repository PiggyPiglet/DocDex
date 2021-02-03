package me.piggypiglet.docdex.bot.commands.implementations;

import com.google.common.collect.Lists;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.util.Types;
import me.piggypiglet.docdex.bot.commands.framework.BotCommand;
import me.piggypiglet.docdex.bot.embed.pagination.PaginationManager;
import me.piggypiglet.docdex.bot.embed.pagination.objects.Pagination;
import me.piggypiglet.docdex.bot.embed.utils.EmbedUtils;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.db.server.Server;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
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
            .setColor(EmbedUtils.COLOUR)
            .build();

    private final Set<Server> servers;
    private final Server defaultServer;
    private final Config config;
    private final PaginationManager paginationManager;

    @Inject
    public JavadocsCommand(@NotNull final Set<Server> servers, @NotNull @Named("default") final Server defaultServer,
                           @NotNull final Config config, @NotNull final PaginationManager paginationManager) {
        super(Set.of("javadocs", "docs"), "", "Get a list of javadocs.");
        this.servers = servers;
        this.defaultServer = defaultServer;
        this.config = config;
        this.paginationManager = paginationManager;
    }


    @Override
    protected RestAction<Message> execute(final @NotNull User user, final @NotNull Message message) {
        final Server server;

        if (message.isFromGuild()) {
            server = servers.stream()
                    .filter(element -> element.getId().equals(message.getGuild().getId()))
                    .findAny().orElse(defaultServer);
        } else {
            server = defaultServer;
        }

        final String uri = config.getUrl() + "/javadocs";
        final HttpRequest request = HttpRequest.newBuilder(URI.create(uri))
                .build();
        final String json;

        try {
            json = CLIENT.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (InterruptedException exception) {
            LOGGER.error("Interrupted.", exception);
            Thread.currentThread().interrupt();
            return null;
        } catch (IOException exception) {
            LOGGER.error("Something went wrong when connecting to " + uri, exception);
            return null;
        }

        final Set<Javadoc> javadocs = GSON.fromJson(json, JAVADOC_SET);

        final Map<Map.Entry<String, String>, Set<Javadoc>> categories = server.getJavadocCategories().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> Map.entry(entry.getKey(), entry.getValue().getDescription()),
                        entry -> entry.getValue().getJavadocs().stream()
                                .map(javadocName -> javadocs.stream()
                                        .filter(javadoc -> javadoc.getNames().stream().anyMatch(javadocName::equalsIgnoreCase))
                                        .findAny()
                                        .orElse(null))
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet())));

        final List<MessageEmbed> pages = categories.entrySet().stream()
                .flatMap(entry -> {
                    final String name = entry.getKey().getKey();
                    final String description = entry.getKey().getValue();

                    final List<List<String>> partitions = Lists.partition(entry.getValue().stream()
                            .map(Javadoc::getNames)
                            .map(names -> "• " + String.join("/", names))
                            .sorted()
                            .collect(Collectors.toUnmodifiableList()), 30);

                    return partitions.stream()
                            .map(JavadocsCommand::format)
                            .map(page -> new EmbedBuilder(EMBED)
                                    .setAuthor(name + ':', null, EmbedUtils.ICON)
                                    .setDescription(description + "\n```\n" + page + "```")
                                    .build());
                }).collect(Collectors.toUnmodifiableList());

        final Pagination pagination = Pagination.builder()
                .pages(pages)
                .author(user.getId())
                .build();

        return Optional.ofNullable(pagination.send(message)).map(messageRestAction -> messageRestAction.map(success -> {
            paginationManager.addPaginatedMessage(success.getId(), pagination);
            return success;
        })).orElse(null);
}

    @NotNull
    private static String format(@NotNull final List<String> strings) {
        if (strings.size() <= 15) {
            return String.join("\n", strings);
        }

        final int length = strings.stream()
                .map(String::length)
                .max(Comparator.comparingInt(i -> i))
                .orElse(7) + 3;
        final StringBuilder builder = new StringBuilder();
        final List<List<String>> partitions = Lists.partition(strings, 15);
        final List<String> first = partitions.get(0);
        final List<String> second = partitions.get(1);

        for (int i = 0; i < 15; ++i) {
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
