package me.piggypiglet.docdex.bot.commands.implementations;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import me.piggypiglet.docdex.bot.commands.JDACommand;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.documentation.DocumentedObjectSerializer;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.scanning.annotations.Hidden;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Pattern;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Hidden
public final class DocumentationCommand extends JDACommand {
    private static final Pattern DISALLOWED_CHARACTERS = Pattern.compile("[^a-zA-Z0-9.$%_# ]");
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private final String url;
    private final String defaultJavadoc;

    @Inject
    public DocumentationCommand(@NotNull final Config config) {
        super("doc", "search a javadoc");
        this.url = config.getUrl();
        this.defaultJavadoc = config.getDefaultJavadoc();
    }

    @Override
    protected void execute(final @NotNull User user, final @NotNull Message message,
                           final @NotNull String @NotNull [] args) {
        final MessageChannel channel = message.getChannel();

        if (DISALLOWED_CHARACTERS.matcher(String.join(" ", args)).find()) {
            channel.sendMessage("You have disallowed characters in your query. Allowed characters: `a-zA-Z0-9.$%_# `")
                    .queue();
            return;
        }

        if (args.length == 0) {
            channel.sendMessage("Incorrect usage. Correct usage is: doc [javadoc] <query>").queue();
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
                    final MessageEmbed embed = DocumentedObjectSerializer.toEmbed(javadoc, object);
                    channel.sendMessage(embed).queue();
                });
    }

    @NotNull
    private static String formatQuery(@NotNull final String query) {
        return query.replace("#", "~").replace("%", "-");
    }
}
