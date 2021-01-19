package me.piggypiglet.docdex.bot.utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.PermissionException;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class PermissionUtils {
    private PermissionUtils() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    public static void sendPermissionError(@NotNull final Message message, @NotNull final Permission permission) {
        final String error = "I'm missing the permission: " + permission;

        if (permission == Permission.MESSAGE_WRITE) {
            try {
                message.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(error).queue());
            } catch (PermissionException exception) {
                // nothing we can really do in this circumstance
            }

            return;
        }

        message.getChannel().sendMessage(error)
                .queue(sentMessage -> sentMessage.delete().queueAfter(15, TimeUnit.SECONDS));
    }
}
