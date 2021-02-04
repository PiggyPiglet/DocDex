package me.piggypiglet.docdex.bot.commands.implementations.documentation;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bot.embed.documentation.type.TypeComponentSerializer;
import me.piggypiglet.docdex.bot.embed.documentation.type.TypeComponents;
import me.piggypiglet.docdex.bot.embed.pagination.PaginationManager;
import me.piggypiglet.docdex.bot.embed.pagination.objects.Pagination;
import me.piggypiglet.docdex.bot.embed.utils.EmbedUtils;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.documentation.DocDexHttp;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedObjectResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TypeComponentCommand extends DocumentationCommand {
    private final PaginationManager paginationManager;

    @Inject
    public TypeComponentCommand(@NotNull final DocDexHttp docDexHttp, @NotNull final PaginationManager paginationManager) {
        super(
                Arrays.stream(TypeComponents.values())
                        .map(TypeComponents::toString)
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet()),
                "Get information on a specific component of a type.",
                docDexHttp
        );

        this.paginationManager = paginationManager;
    }

    @Nullable
    @Override
    protected RestAction<Message> execute(final @NotNull Message message, @NotNull final List<Map.Entry<DocumentedObjectResult, EmbedBuilder>> objects,
                                          final boolean returnFirst, @NotNull final Server server) {
        if (returnFirst) {
            final Map.Entry<DocumentedObjectResult, EmbedBuilder> entry = objects.get(0);
            final DocumentedObject object = entry.getKey().getObject();
            final EmbedBuilder defaultEmbed = entry.getValue();

            final String upperMessage = message.getContentRaw().toUpperCase().replace(server.getPrefix().toUpperCase(), "");
            final TypeComponents component = TypeComponents.valueOf(upperMessage.substring(0, upperMessage.indexOf(' ')));
            final List<MessageEmbed> pages = TypeComponentSerializer.toEmbeds(object, component).stream()
                    .map(embed -> {
                        EmbedUtils.merge(defaultEmbed, embed);
                        return embed.build();
                    })
                    .collect(Collectors.toList());
            final MessageChannel channel = message.getChannel();

            if (pages.size() > 9) {
                return channel.sendMessage("There are too many " + component.getFormattedPlural().toLowerCase() + " to display in a paginated message. Please refer to the web page: <" + object.getLink() + '>');
            }

            if (pages.isEmpty()) {
                return channel.sendMessage(object.getName() + " does not have any " + component.getFormattedPlural().toLowerCase() + '.');
            }

            if (pages.size() == 1) {
                return channel.sendMessage(pages.get(0));
            }

            final Pagination pagination = Pagination.builder()
                    .pages(pages)
                    .author(message.getAuthor().getId())
                    .build();

            return pagination.send(message, paginationManager);
        }

        final String suggestions = objects.stream()
                .map(Map.Entry::getKey)
                .map(DocumentedObjectResult::getName)
                .collect(Collectors.joining("\n"));

        return message.getChannel().sendMessage("There was no direct match for that query, did you mean any of the following?: ```\n" + suggestions + "```");
    }
}
