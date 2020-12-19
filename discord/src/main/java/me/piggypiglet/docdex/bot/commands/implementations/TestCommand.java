package me.piggypiglet.docdex.bot.commands.implementations;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bot.commands.JDACommand;
import me.piggypiglet.docdex.bot.embed.pagination.PaginationManager;
import me.piggypiglet.docdex.bot.embed.pagination.objects.Pagination;
import me.piggypiglet.docdex.bot.emote.EmoteWrapper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TestCommand extends JDACommand {
    private static final Pagination PAGINATION = Pagination.builder()
            .page(EmoteWrapper.from(":one:"), new EmbedBuilder()
                    .setTitle("one")
                    .build())
            .page(EmoteWrapper.from(":two:"), new EmbedBuilder()
                    .setTitle("two")
                    .build())
            .page(EmoteWrapper.from(":three:"), new EmbedBuilder()
                    .setTitle("three")
                    .build())
            .build();

    private final PaginationManager paginationManager;

    @Inject
    public TestCommand(@NotNull final PaginationManager paginationManager) {
        super(Set.of("test"), "test");
        this.paginationManager = paginationManager;
    }

    @Override
    protected void execute(final @NotNull User user, final @NotNull Message message) {
        Optional.ofNullable(PAGINATION.send(message.getChannel()))
                .ifPresent(action -> action.queue(newMessage -> paginationManager.addPaginatedMessage(newMessage.getId(), PAGINATION.getPages())));
    }
}
