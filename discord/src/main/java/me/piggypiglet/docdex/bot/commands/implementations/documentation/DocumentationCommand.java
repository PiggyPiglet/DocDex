package me.piggypiglet.docdex.bot.commands.implementations.documentation;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.piggypiglet.docdex.bot.commands.JDACommand;
import me.piggypiglet.docdex.bot.embed.documentation.SimpleObjectSerializer;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Set;
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

    private final String url;
    private final String defaultJavadoc;

    protected DocumentationCommand(@NotNull final String @NotNull [] matches, @NotNull final String url,
                                   @NotNull final String defaultJavadoc) {
        this(matches, "", url, defaultJavadoc);
    }

    protected DocumentationCommand(@NotNull final String @NotNull [] matches, @NotNull final String description,
                                   @NotNull final String url, @NotNull final String defaultJavadoc) {
        this(Arrays.stream(matches).collect(Collectors.toSet()), description, url, defaultJavadoc);
    }

    protected DocumentationCommand(@NotNull final Set<String> matches, @NotNull final String url,
                                   @NotNull final String defaultJavadoc) {
        this(matches, "", url, defaultJavadoc);
    }

    protected DocumentationCommand(@NotNull final Set<String> matches, @NotNull final String description,
                                   @NotNull final String url, @NotNull final String defaultJavadoc) {
        super(matches, description);
        this.url = url;
        this.defaultJavadoc = defaultJavadoc;
    }

    @Override
    public void run(final @NotNull User user, final @NotNull Message message,
                    final @NotNull String start) {
        final String[] args = args(message, start);
        final MessageChannel channel = message.getChannel();

        if (DISALLOWED_CHARACTERS.matcher(String.join(" ", args)).find()) {
            channel.sendMessage("You have disallowed characters in your query. Allowed characters: `a-zA-Z0-9.$%_# `")
                    .queue();
            return;
        }

        if (args.length == 0) {
            channel.sendMessage("Incorrect usage. Correct usage is: " + start + " [javadoc] <query>").queue();
            return;
        }

        final String javadoc;
        final String query;

        if (args.length >= 2) {
            javadoc = args[0];
            query = args[1];
        } else {
            javadoc = defaultJavadoc;
            query = args[0];
        }

        final HttpRequest request = HttpRequest.newBuilder(URI.create(url + "?javadoc=" + javadoc + "&query=" + formatQuery(query)))
                .build();
        CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    if (json.equalsIgnoreCase("null")) {
                        channel.sendMessage("Unknown javadoc: " + javadoc + '.').queue();
                        return;
                    }

                    final DocumentedObject object = GSON.fromJson(json, DocumentedObject.class);
                    execute(message, SimpleObjectSerializer.toEmbed(javadoc, object), object);
                });
    }

    protected abstract void execute(final @NotNull Message message, @NotNull final EmbedBuilder defaultEmbed,
                                    final @NotNull DocumentedObject object);

    @NotNull
    private static String formatQuery(@NotNull final String query) {
        return query.replace("#", "~").replace("%", "-");
    }
}
