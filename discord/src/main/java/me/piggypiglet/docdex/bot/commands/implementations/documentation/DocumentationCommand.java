package me.piggypiglet.docdex.bot.commands.implementations.documentation;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.util.Types;
import me.piggypiglet.docdex.bot.commands.JDACommand;
import me.piggypiglet.docdex.bot.embed.documentation.SimpleObjectSerializer;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.documentation.IndexURLBuilder;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.utils.DataUtils;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class DocumentationCommand extends JDACommand {
    private static final Pattern DISALLOWED_CHARACTERS = Pattern.compile("[^a-zA-Z0-9.$%_# ]");
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
    private static final Type OBJECT_LIST = Types.listOf(DocumentedObject.class);

    private final Config config;

    protected DocumentationCommand(@NotNull final String @NotNull [] matches, @NotNull final String description,
                                   @NotNull final Config config) {
        this(Arrays.stream(matches).collect(Collectors.toSet()), description, config);
    }

    protected DocumentationCommand(@NotNull final Set<String> matches, @NotNull final String description,
                                   @NotNull final Config config) {
        super(matches, "[javadoc] <query> [limit/$(first result)]", description);
        this.config = config;
    }

    @Override
    public void run(final @NotNull User user, final @NotNull Message message,
                    final @NotNull String start) {
        final String[] args = args(message, start);
        final MessageChannel channel = message.getChannel();

        if (DISALLOWED_CHARACTERS.matcher(String.join(" ", args)).find()) {
            channel.sendMessage("You have disallowed characters in your query. Allowed characters: `a-zA-Z0-9.$%_# `").queue();
            return;
        }

        if (args.length == 0 || args[0].isBlank()) {
            channel.sendMessage("Incorrect usage. Correct usage is: " + start + " [javadoc] <query> [limit/$(first result)]").queue();
            return;
        }

        final AtomicInteger limit = new AtomicInteger(-1);
        final AtomicBoolean returnClosest = new AtomicBoolean();

        try {
            String limitArg = null;

            if (args.length > 2) {
                limitArg = args[2];
            } else if (args.length == 2) {
                limitArg = args[1];
            }

            if (limitArg != null) {
                if (limitArg.equals("$")) {
                    returnClosest.set(true);
                } else {
                    limit.set(Integer.parseInt(limitArg));
                }
            }
        } catch (Exception e) {}

        final String javadoc;
        final String query;

        if (args.length >= 2) {
            if (limit.get() != -1 || returnClosest.get()) {
                javadoc = config.getDefaultJavadoc();
                query = args[0];
            } else {
                javadoc = args[0];
                query = args[1];
            }
        } else {
            javadoc = config.getDefaultJavadoc();
            query = args[0];
        }

        final IndexURLBuilder urlBuilder = new IndexURLBuilder()
                .javadoc(javadoc)
                .query(query);

        if (limit.get() != -1) {
            urlBuilder.limit(limit.get());
        }

        final HttpRequest request = HttpRequest.newBuilder(URI.create(config.getUrl() + urlBuilder.build()))
                .build();
        CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    if (json.equalsIgnoreCase("null")) {
                        channel.sendMessage("Unknown javadoc: " + javadoc + '.').queue();
                        return;
                    }

                    final List<DocumentedObject> objects = GSON.fromJson(json, OBJECT_LIST);

                    if ((objects.size() == 1 && limit.get() != 1) || returnClosest.get()) {
                        final DocumentedObject object = objects.get(0);
                        execute(message, SimpleObjectSerializer.toEmbed(user, javadoc, object), object);
                        return;
                    }

                    final String suggestions = objects.stream()
                            .map(DataUtils::getFqn)
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

    private static void queueAndDelete(@NotNull final MessageAction message) {
        message.queue(sentMessage -> sentMessage.delete().queueAfter(15, TimeUnit.SECONDS));
    }
}
