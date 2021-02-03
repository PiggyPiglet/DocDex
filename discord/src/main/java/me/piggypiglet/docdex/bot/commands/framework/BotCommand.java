package me.piggypiglet.docdex.bot.commands.framework;

import me.piggypiglet.docdex.db.server.Server;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class BotCommand {
    protected static final Logger LOGGER = LoggerFactory.getLogger("Commands");
    protected static final Consumer<Throwable> ERROR_LOG = throwable -> LOGGER.error("", throwable);
    private static final Pattern SPACE_DELIMITER = Pattern.compile(" ");

    private final Set<String> matches;
    private final String usage;
    private final String description;

    protected BotCommand(@NotNull final String @NotNull [] matches, @NotNull final String usage,
                         @NotNull final String description) {
        this(Arrays.stream(matches).collect(Collectors.toSet()), usage, description);
    }

    protected BotCommand(@NotNull final Set<String> matches, @NotNull final String usage,
                         @NotNull final String description) {
        this.matches = matches;
        this.usage = usage;
        this.description = description;
    }

    @Nullable
    protected RestAction<Message> execute(@NotNull final User user, @NotNull final Message message) {
        return null;
    }

    @Nullable
    protected RestAction<Message> execute(@NotNull final User user, @NotNull final Message message,
                           @NotNull final List<String> args) {
        return null;
    }

    @Nullable
    public RestAction<Message> run(@NotNull final User user, @NotNull final Message message,
                    @NotNull final Server server, final int start) {
        final RestAction<Message> attemptOne = execute(user, message);

        if (attemptOne != null) {
            return attemptOne;
        }

        return execute(user, message, args(message, start));
    }

    @NotNull
    public Set<String> getMatches() {
        return matches;
    }

    @NotNull
    public String getUsage() {
        return usage;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    @NotNull
    protected List<String> args(@NotNull final Message message, final int start) {
        return Arrays.asList(SPACE_DELIMITER.split(message.getContentRaw().substring(start).trim()));
    }
}
