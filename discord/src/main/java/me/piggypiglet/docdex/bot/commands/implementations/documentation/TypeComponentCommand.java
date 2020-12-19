package me.piggypiglet.docdex.bot.commands.implementations.documentation;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bot.embed.documentation.type.TypeComponentSerializer;
import me.piggypiglet.docdex.bot.embed.documentation.type.TypeComponents;
import me.piggypiglet.docdex.bot.embed.pagination.PaginationManager;
import me.piggypiglet.docdex.bot.embed.pagination.objects.Pagination;
import me.piggypiglet.docdex.bot.embed.utils.EmbedUtils;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TypeComponentCommand extends DocumentationCommand {
    private final String upperPrefix;
    private final PaginationManager paginationManager;

    @Inject
    public TypeComponentCommand(@NotNull final Config config, @NotNull final PaginationManager paginationManager) {
        super(
                Arrays.stream(TypeComponents.values())
                        .map(TypeComponents::toString)
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet()),
                "get information on a specific component of a type.",
                config.getUrl(), config.getDefaultJavadoc()
        );
        this.upperPrefix = config.getPrefix().toUpperCase();
        this.paginationManager = paginationManager;
    }

    @Override
    protected void execute(final @NotNull Message message, final @NotNull EmbedBuilder defaultEmbed,
                           final @NotNull DocumentedObject object) {
        final String upperMessage = message.getContentRaw().toUpperCase().replace(upperPrefix, "");
        final TypeComponents component = TypeComponents.valueOf(upperMessage.substring(0, upperMessage.indexOf(' ')));
        final List<MessageEmbed> pages = TypeComponentSerializer.serialize(object, component).stream()
                .peek(embed -> EmbedUtils.merge(defaultEmbed, embed))
                .map(EmbedBuilder::build)
                .collect(Collectors.toList());
        final MessageChannel channel = message.getChannel();

        if (pages.size() == 1) {
            channel.sendMessage(pages.get(0)).queue();
            return;
        }

        final Pagination pagination = Pagination.builder()
                .pages(pages)
                .build();
        Optional.ofNullable(pagination.send(channel)).ifPresent(action ->
                action.queue(sentMessage -> paginationManager.addPaginatedMessage(sentMessage.getId(), pagination.getPages()), ERROR_LOG));
    }
}
