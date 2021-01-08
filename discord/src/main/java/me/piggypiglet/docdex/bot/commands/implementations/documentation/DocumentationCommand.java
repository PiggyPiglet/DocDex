package me.piggypiglet.docdex.bot.commands.implementations.documentation;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.util.Types;
import me.piggypiglet.docdex.bot.commands.framework.BotCommand;
import me.piggypiglet.docdex.bot.embed.documentation.DocumentationObjectSerializer;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.documentation.IndexURLBuilder;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedObjectResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class DocumentationCommand extends BotCommand {
    private static final Pattern DISALLOWED_CHARACTERS = Pattern.compile("[^a-zA-Z0-9.$%_#\\-, ()]");
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
    private static final Type OBJECT_LIST = Types.listOf(DocumentedObjectResult.class);

    private final Config config;

    protected DocumentationCommand(@NotNull final String @NotNull [] matches, @NotNull final String description,
                                   @NotNull final Config config) {
        this(Arrays.stream(matches).collect(Collectors.toSet()), description, config);
    }

    protected DocumentationCommand(@NotNull final Set<String> matches, @NotNull final String description,
                                   @NotNull final Config config) {
        super(matches, "[javadoc] [limit/$(first result)] <query>", description);
        this.config = config;
    }

    @Override
    public void run(final @NotNull User user, final @NotNull Message message,
                    final int start) {
        final List<String> args = args(message, start);
        final MessageChannel channel = message.getChannel();

        if (DISALLOWED_CHARACTERS.matcher(String.join(" ", args)).find()) {
            queueAndDelete(channel.sendMessage("You have disallowed characters in your query. Allowed characters: `a-zA-Z0-9.$%_#-, ()`"));
            return;
        }

        if (args.size() == 0 || args.get(0).isBlank()) {
            queueAndDelete(channel.sendMessage("**Incorrect usage:**\nCorrect usage is: " +
                    message.getContentRaw().substring(0, start) + " [javadoc] [limit/$(first result)] <query>"));
            return;
        }

        final AtomicReference<String> javadoc = new AtomicReference<>(config.getDefaultJavadoc());
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
                    queueAndDelete(message.getChannel().sendMessage("Invalid limit."));
                    return;
                }
            }

            query = String.join(" ", args.subList(2, args.size()));
        }

        final IndexURLBuilder urlBuilder = new IndexURLBuilder()
                .javadoc(javadoc.get().toLowerCase())
                .query(query.replace(" ", "%20"));

        if (limit.get() != 0) {
            urlBuilder.limit(limit.get());
        }

        final HttpRequest request = HttpRequest.newBuilder(URI.create(config.getUrl() + urlBuilder.build()))
                .build();
        CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    if (json.equalsIgnoreCase("null")) {
                        queueAndDelete(channel.sendMessage("Unknown javadoc: " + javadoc.get() + '.'));
                        return;
                    }

                    final List<DocumentedObjectResult> objects = GSON.fromJson(json, OBJECT_LIST);

                    if ((objects.size() == 1 && limit.get() == 0) || returnClosest.get()) {
                        final DocumentedObject object = objects.get(0).getObject();

                        final String error = checkAndReturnError(object);
                        if (!error.isBlank()) {
                            message.getChannel().sendMessage(error).queue();
                            return;
                        }

                        execute(message, DocumentationObjectSerializer.toEmbed(user, javadoc.get(), object), object);
                        return;
                    }

                    final String suggestions = objects.stream()
                            .map(DocumentedObjectResult::getName)
                            .collect(Collectors.joining("\n"));

                    channel.sendMessage("There was no direct match for that query, did you mean any of the following?: ```\n" + suggestions + "```")
                            .queue();
                }).exceptionally(throwable -> {
                    LOGGER.error("", throwable);
                    return null;
                });
    }

    protected abstract void execute(final @NotNull Message message, @NotNull final EmbedBuilder defaultEmbed,
                                    final @NotNull DocumentedObject object);

    @NotNull
    protected String checkAndReturnError(@NotNull final DocumentedObject object) {
        return "";
    }

    private static void queueAndDelete(@NotNull final MessageAction message) {
        message.queue(sentMessage -> sentMessage.delete().queueAfter(20, TimeUnit.SECONDS));
    }
}
