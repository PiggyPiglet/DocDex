package me.piggypiglet.docdex.bot.commands.implementations;

import com.google.gson.Gson;
import com.google.inject.Inject;
import me.piggypiglet.docdex.bot.commands.JDACommand;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.documentation.DocumentedObjectSerializer;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DocumentationCommand extends JDACommand {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

    private final String url;

    @Inject
    public DocumentationCommand(@NotNull final Config config) {
        super("doc", "search a javadoc");
        this.url = config.getUrl();
    }

    @Override
    protected void execute(final @NotNull User user, final @NotNull Message message,
                           final @NotNull String @NotNull [] args) {
        final MessageChannel channel = message.getChannel();

        if (args.length < 2) {
            channel.sendMessage("Incorrect usage. Correct usage is: doc <javadoc> <query>")
                    .queue(error -> error.delete().queueAfter(15, TimeUnit.SECONDS));
            return;
        }

        final HttpRequest request = HttpRequest.newBuilder(URI.create(url + "?javadoc=" + args[0] + "&query=" + args[1].replace("#", "~").replace("%", "-")))
                .build();
        CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(json -> GSON.fromJson(json, DocumentedObject.class))
                .thenApply(object -> DocumentedObjectSerializer.toEmbed(args[0], object))
                .thenAccept(embed -> channel.sendMessage(embed).queue());
    }
}
