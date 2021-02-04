package me.piggypiglet.docdex.bot.commands.implementations.documentation;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bot.embed.documentation.SimpleObjectSerializer;
import me.piggypiglet.docdex.bot.embed.pagination.PaginationManager;
import me.piggypiglet.docdex.bot.embed.pagination.objects.Pagination;
import me.piggypiglet.docdex.bot.embed.utils.EmbedUtils;
import me.piggypiglet.docdex.documentation.DocDexHttp;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedObjectResult;
import me.piggypiglet.docdex.scanning.annotations.Hidden;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
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
    public SimpleCommand(@NotNull final DocDexHttp docDexHttp, @NotNull final PaginationManager paginationManager) {
        super(Set.of("search"), "Search a javadoc.", docDexHttp);
        this.paginationManager = paginationManager;
    }

    @Nullable
    @Override
    protected RestAction<Message> execute(final @NotNull Message message, @NotNull final List<Map.Entry<DocumentedObjectResult, EmbedBuilder>> objects,
                                          final boolean returnFirst) {
        final List<MessageEmbed> pages = objects.stream().map(entry -> {
            final DocumentedObject object = entry.getKey().getObject();
            final EmbedBuilder embed = SimpleObjectSerializer.toEmbed(object);
            EmbedUtils.merge(entry.getValue(), embed);
            return embed.build();
        }).collect(Collectors.toList());

        if (returnFirst) {
            return message.getChannel().sendMessage(pages.get(0));
        }

        final Pagination pagination = Pagination.builder()
                .pages(pages)
                .author(message.getAuthor().getId())
                .build();

        return pagination.send(message, paginationManager);
    }
}
