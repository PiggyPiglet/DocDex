package me.piggypiglet.docdex.bot.commands.implementations;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import me.piggypiglet.docdex.bot.commands.framework.BotCommand;
import me.piggypiglet.docdex.bot.embed.pagination.PaginationManager;
import me.piggypiglet.docdex.bot.embed.pagination.objects.Pagination;
import me.piggypiglet.docdex.bot.embed.utils.EmbedUtils;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.documentation.DocDexHttp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JavadocsCommand extends BotCommand {
    private static final MessageEmbed EMBED = new EmbedBuilder()
            .setColor(EmbedUtils.COLOUR)
            .build();

    private final DocDexHttp docDexHttp;
    private final PaginationManager paginationManager;

    @Inject
    public JavadocsCommand(@NotNull final DocDexHttp docDexHttp, @NotNull final PaginationManager paginationManager) {
        super(Set.of("javadocs", "docs"), "", "Get a list of javadocs.");

        this.docDexHttp = docDexHttp;
        this.paginationManager = paginationManager;
    }


    @Override
    protected RestAction<Message> execute(final @NotNull User user, final @NotNull Message message,
                                          @NotNull final Server server) {
        final Set<Javadoc> javadocs = docDexHttp.getJavadocs();

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

        return pagination.send(message, paginationManager);
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
