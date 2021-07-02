package me.piggypiglet.docdex.bot.commands.implementations.documentation;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.util.Types;
import me.piggypiglet.docdex.bot.commands.framework.BotCommand;
import me.piggypiglet.docdex.bot.embed.documentation.DocumentationObjectSerializer;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.documentation.DocDexHttp;
import me.piggypiglet.docdex.documentation.IndexURLBuilder;
import me.piggypiglet.docdex.documentation.objects.DocumentedObjectResult;
import me.piggypiglet.docdex.documentation.response.ObjectListResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class DocumentationCommand extends BotCommand {
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int BAD_GATEWAY = 502;

    private static final Pattern DISALLOWED_CHARACTERS = Pattern.compile("[^a-zA-Z0-9.$%_#\\-, ()]");
    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("([ ](?![^(]*\\)))");
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private static final Type OBJECT_LIST = Types.listOf(DocumentedObjectResult.class);

    private final DocDexHttp docDexHttp;

    protected DocumentationCommand(@NotNull final Set<String> matches, @NotNull final String description,
                                   @NotNull final DocDexHttp docDexHttp) {
        super(matches, "[javadoc] [limit/$(first result)] <query>", description);

        this.docDexHttp = docDexHttp;
    }

    @Nullable
    @Override
    public RestAction<Message> run(final @NotNull User user, final @NotNull Message message,
                                   @NotNull final Server server, final int start) {
        final List<String> args = args(message, start);
        final MessageChannel channel = message.getChannel();

        if (DISALLOWED_CHARACTERS.matcher(String.join(" ", args)).find()) {
            queueAndDelete(() -> channel.sendMessage(user.getAsMention() + "\nYou have disallowed characters in your query. Allowed characters: `a-zA-Z0-9.$%_#-, ()`"), message);
            return null;
        }

        if (args.isEmpty() || args.get(0).isBlank()) {
            queueAndDelete(() -> channel.sendMessage("**Incorrect usage:** " + user.getAsMention() + "\nCorrect usage is: " +
                    message.getContentRaw().substring(0, start) + "[javadoc] [limit/$(first result)] <query>"), message);
            return null;
        }

        final AtomicReference<String> javadoc = new AtomicReference<>(server.getDefaultJavadoc());
        final AtomicInteger limit = new AtomicInteger();
        final AtomicBoolean returnClosest = new AtomicBoolean(false);
        final String query;

        if (args.size() == 1) {
            query = String.join(" ", args);
        } else if (args.size() == 2) {
            final String first = args.get(0);

            try {
                limit.set(Integer.parseInt(first));
            } catch (NumberFormatException exception) {
                if (first.equals("$")) {
                    returnClosest.set(true);
                } else {
                    javadoc.set(first);
                }
            }

            query = String.join(" ", args.subList(1, args.size()));
        } else {
            javadoc.set(args.get(0));

            final String second = args.get(1);

            try {
                limit.set(Integer.parseInt(second));
            } catch (NumberFormatException exception) {
                if (second.equals("$")) {
                    returnClosest.set(true);
                } else {
                    queueAndDelete(() -> message.getChannel().sendMessage("Invalid limit: " + second), message);
                    return null;
                }
            }

            query = String.join(" ", args.subList(2, args.size()));
        }

        final IndexURLBuilder urlBuilder = new IndexURLBuilder()
                .javadoc(javadoc.get().toLowerCase())
                .query(query.replace(" ", "%20"))
                .algorithm(server.getAlgorithm());

        if (limit.get() != 0) {
            urlBuilder.limit(limit.get());
        }

        final ObjectListResult result = docDexHttp.getObjects(urlBuilder.build());

        if (result == null) {
            queueAndDelete(() -> channel.sendMessage("Something went very wrong, " + urlBuilder), message);
            return null;
        }

        final List<DocumentedObjectResult> objectResults = result.getResults();

        if (objectResults == null) {
            queueAndDelete(() -> channel.sendMessage(result.getError()), message);
            return null;
        }

        final List<Map.Entry<DocumentedObjectResult, EmbedBuilder>> objects = objectResults.stream()
                .map(objectResult -> Map.entry(objectResult, DocumentationObjectSerializer.toEmbed(user, javadoc.get(), objectResult.getObject())))
                .collect(Collectors.toList());
        final boolean returnFirst = objects.size() == 1 && limit.get() == 0 || returnClosest.get();

        return Stream.of(
                execute(message, objects, returnFirst),
                execute(message, objects, returnFirst, server)
        ).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Nullable
    protected RestAction<Message> execute(final @NotNull Message message, final @NotNull List<Map.Entry<DocumentedObjectResult, EmbedBuilder>> objects,
                                          final boolean returnFirst) {
        return null;
    }

    @Nullable
    protected RestAction<Message> execute(final @NotNull Message message, final @NotNull List<Map.Entry<DocumentedObjectResult, EmbedBuilder>> objects,
                                          final boolean returnFirst, @NotNull final Server server) {
        return null;
    }

    @NotNull
    @Override
    protected List<String> args(final @NotNull Message message, final int start) {
        return Arrays.asList(ARGUMENT_PATTERN.split(message.getContentStripped().substring(start).trim()));
    }

    private static void queueAndDelete(@NotNull final Supplier<MessageAction> messageSupplier, @NotNull final Message request) {
        queue(
                create(messageSupplier::get, request),
                success -> queueAfter(create(success::delete, request), request, 20, TimeUnit.SECONDS),
                request
        );
    }
}
