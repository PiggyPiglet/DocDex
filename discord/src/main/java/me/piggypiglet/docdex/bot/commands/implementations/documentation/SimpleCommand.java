package me.piggypiglet.docdex.bot.commands.implementations.documentation;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bot.embed.documentation.SimpleObjectSerializer;
import me.piggypiglet.docdex.bot.embed.pagination.PaginationManager;
import me.piggypiglet.docdex.bot.embed.pagination.objects.Pagination;
import me.piggypiglet.docdex.bot.embed.utils.EmbedUtils;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedObjectResult;
import me.piggypiglet.docdex.scanning.annotations.Hidden;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Hidden
public final class SimpleCommand extends DocumentationCommand {
    private final PaginationManager paginationManager;

    @Inject
    public SimpleCommand(@NotNull final Config config, @NotNull final PaginationManager paginationManager) {
        super(Set.of("search"), "Search a javadoc.", config);
        this.paginationManager = paginationManager;
    }

    @Override
    protected void execute(final @NotNull Message message, @NotNull final List<Map.Entry<DocumentedObjectResult, EmbedBuilder>> objects,
                           final boolean returnFirst) {
        final List<MessageEmbed> pages = objects.stream()
                .map(entry -> {
                    final DocumentedObject object = entry.getKey().getObject();
                    final EmbedBuilder embed = SimpleObjectSerializer.toEmbed(object);
                    EmbedUtils.merge(entry.getValue(), embed);
                    return embed.build();
                })
                .map(embed -> {
                    final String error = checkAndReturnError(embed);

                    if (!error.isBlank()) {
                        return new EmbedBuilder()
                                .setTitle("Error!")
                                .setColor(Color.RED)
                                .setDescription(error)
                                .build();
                    }

                    return embed;
                }).collect(Collectors.toList());

        if (returnFirst) {
            message.getChannel().sendMessage(pages.get(0)).queue();
            return;
        }

        final Pagination pagination = Pagination.builder()
                .pages(pages)
                .author(message.getAuthor().getId())
                .build();
        Optional.ofNullable(pagination.send(message.getChannel())).ifPresent(action -> action.queue(success ->
                paginationManager.addPaginatedMessage(success.getId(), pagination)));
    }

    @NotNull
    protected String checkAndReturnError(final @NotNull MessageEmbed embed) {
        if (embed.getDescription().length() > 1024) {
            return "This object is too big to be viewed in discord, please refer to it's javadoc page: " + embed.getUrl();
        }

        return "";
    }
}
