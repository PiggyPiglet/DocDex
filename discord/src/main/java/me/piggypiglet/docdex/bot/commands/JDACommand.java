package me.piggypiglet.docdex.bot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class JDACommand {
    private static final Pattern SPACE_DELIMITER = Pattern.compile(" ");

    private final String match;
    private final String description;

    protected JDACommand(@NotNull final String match) {
        this(match, "");
    }

    protected JDACommand(@NotNull final String match, @NotNull final String description) {
        this.match = match;
        this.description = description;
    }

    protected void execute(@NotNull final User user, @NotNull final Message message) {}

    protected void execute(@NotNull final User user, @NotNull final Message message,
                           @NotNull final String @NotNull [] args) {}

    public void execute(@NotNull final User user, @NotNull final Message message,
                        @NotNull final String start) {
        execute(user, message);
        execute(user, message, args(message, start));
    }

    @NotNull
    public String getMatch() {
        return match;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    @NotNull
    private static String @NotNull [] args(@NotNull final Message message, @NotNull final String start) {
        return SPACE_DELIMITER.split(message.getContentRaw().replace(start, "").trim());
    }
}
