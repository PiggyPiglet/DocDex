package me.piggypiglet.docdex.bot.commands.implementations;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import me.piggypiglet.docdex.bot.commands.framework.BotCommand;
import me.piggypiglet.docdex.bot.embed.utils.EmbedUtils;
import me.piggypiglet.docdex.config.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class InfoCommand extends BotCommand {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private static final String PIG_URL = "https://piggypiglet.me";
    private static final String PIG_ICON = "https://cdn.piggypiglet.me/personal/img/pigygon.png";

    private static final String DOCDEX_URL = "https://github.com/PiggyPiglet/DocDex";

    private final Config config;

    @Inject
    public InfoCommand(@NotNull final Config config) {
        super(Set.of("info"), "", "Display info about the bot.");
        this.config = config;
    }

    @Override
    protected void execute(final @NotNull User user, final @NotNull Message message) {
        final HttpRequest request = HttpRequest.newBuilder(URI.create(config.getUrl() + "/javadocs"))
                .build();
        CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    final EmbedBuilder embed = new EmbedBuilder();
                    final SnowflakeCacheView<Guild> guilds = user.getJDA().getGuildCache();

                    embed.setTitle("DocDex", DOCDEX_URL);
                    embed.setThumbnail(EmbedUtils.ICON);
                    embed.setColor(EmbedUtils.COLOUR);
                    embed.setDescription(
                            "DocDex (Documentation Index) is a JSON web API which provides an endpoint that returns " +
                            "information on a particular javadoc object, from a fuzzy query. This bot uses this API to " +
                            "provide the information in discord. The bot is made in Java 11 using JDA."
                    );
                    embed.addField("Creator", "[PiggyPiglet#5609](" + PIG_URL + ')', true);
                    embed.addField("Source", "[Github](" + DOCDEX_URL + ')', true);
                    embed.addField("Invite", "https://piggypiglet.me/docdex", true);
                    embed.addField("Servers", String.valueOf(guilds.size()), true);
                    embed.addField("Users", String.valueOf(guilds.stream().mapToInt(Guild::getMemberCount).sum()), true);
                    embed.addField("Javadocs", String.valueOf(JsonParser.parseString(json).getAsJsonArray().size()), true);
                    embed.setFooter("by PiggyPiglet", PIG_ICON);

                    message.getChannel().sendMessage(embed.build()).queue();
                });
    }
}
