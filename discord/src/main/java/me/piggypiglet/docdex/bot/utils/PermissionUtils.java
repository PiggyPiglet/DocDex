package me.piggypiglet.docdex.bot.utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class PermissionUtils {
    private static final String PERMISSIONS_ERROR = "I'm missing at least one of the following permissions: " + Stream.of(
            Permission.MESSAGE_MANAGE,
            Permission.MESSAGE_ADD_REACTION,
            Permission.MESSAGE_EMBED_LINKS,
            Permission.MESSAGE_WRITE,
            Permission.MESSAGE_HISTORY,
            Permission.MESSAGE_READ,
            Permission.VIEW_CHANNEL
    ).map(Permission::getName).collect(Collectors.joining(", "));

    private PermissionUtils() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    public static void sendPermissionError(@NotNull final Message message, @Nullable final Permission permission) {
        final String error = permission == null ? PERMISSIONS_ERROR : "I'm missing the permission: " + permission.getName();

        if (permission == Permission.MESSAGE_WRITE) {
            try {
                message.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(error).queue());
            } catch (PermissionException exception) {
                // nothing we can really do in this circumstance
            }

            return;
        }

        try {
            message.getChannel().sendMessage(error).queue();
        } catch (PermissionException exception) {
            try {
                message.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(error).queue());
            } catch (PermissionException exception2) {
                // nothing we can really do in this circumstance
            }
        }
    }

    @Nullable
    public static <R, T extends RestAction<R>> T create(@NotNull final ThrowingSupplier<T> supplier, @NotNull final Message request) {
        try {
            return supplier.supply();
        } catch (PermissionException exception) {
            sendPermissionError(request, exception.getPermission());
        } catch (ErrorResponseException exception) {
            sendPermissionError(request, null);
        }

        return null;
    }

    public static <T> void queue(@Nullable final RestAction<T> action, @NotNull final Message request) {
        queue(action, t -> {}, request);
    }

    public static <T> void queue(@Nullable final RestAction<T> action, @NotNull final Consumer<T> success,
                                 @NotNull final Message request) {
        if (action == null) {
            return;
        }

        action.queue(success, failure -> handleFailure(failure, request));
    }

    public static <T> void queueAfter(@Nullable final RestAction<T> action, @NotNull final Message request,
                                      final long delay, @NotNull final TimeUnit unit) {
        queueAfter(action, success -> {}, request, delay, unit);
    }

    public static <T> void queueAfter(@Nullable final RestAction<T> action, @NotNull final Consumer<T> success,
                                      @NotNull final Message request, final long delay,
                                      @NotNull final TimeUnit unit) {
        if (action == null) {
            return;
        }

        action.queueAfter(delay, unit, success, failure -> handleFailure(failure, request));
    }

    private static void handleFailure(@NotNull final Throwable failure, @NotNull final Message request) {
        if (failure instanceof PermissionException) {
            sendPermissionError(request, ((PermissionException) failure).getPermission());
        }

        if (failure instanceof ErrorResponseException) {
            if (((ErrorResponseException) failure).getErrorResponse() != ErrorResponse.MISSING_PERMISSIONS) {
                return;
            }

            sendPermissionError(request, null);
        }
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T supply() throws PermissionException, ErrorResponseException;
    }
}
