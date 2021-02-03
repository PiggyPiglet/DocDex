package me.piggypiglet.docdex.bot.commands.implementations;

import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.bot.commands.framework.BotCommand;
import me.piggypiglet.docdex.bot.embed.utils.EmbedUtils;
import me.piggypiglet.docdex.bot.listeners.GuildJoinHandler;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.db.server.Server;
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
import java.text.NumberFormat;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class InfoCommand extends BotCommand {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private static final String GITHUB = "https://github.com/PiggyPiglet/DocDex";
    private static final String INVITE = "https://piggypiglet.me/docdex";
    private static final String PIG_URL = "https://piggypiglet.me";

    private static final NumberFormat FORMATTER = NumberFormat.getInstance();

    private final Config config;
    private final Set<Server> servers;
    private final GuildJoinHandler guildJoinHandler;
    private final Server defaultServer;

    @Inject
    public InfoCommand(@NotNull final Config config, @NotNull final Set<Server> servers,
                       @NotNull final GuildJoinHandler guildJoinHandler, @NotNull @Named("default") final Server defaultServer) {
        super(Set.of("info"), "", "Display info about the bot.");

        this.config = config;
        this.servers = servers;
        this.guildJoinHandler = guildJoinHandler;
        this.defaultServer = defaultServer;
    }

    @Override
    protected void execute(final @NotNull User user, final @NotNull Message message) {
        final Server server;
        if (message.isFromGuild()) {
            final Guild guild = message.getGuild();

            server = servers.stream()
                    .filter(element -> element.getId().equals(guild.getId()))
                    .findAny().orElseGet(() -> guildJoinHandler.joinGuild(guild).join());
        } else {
            server = defaultServer;
        }

        final HttpRequest request = HttpRequest.newBuilder(URI.create(config.getUrl() + "/javadocs"))
                .build();
        CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    final EmbedBuilder embed = new EmbedBuilder();
                    final SnowflakeCacheView<Guild> guilds = user.getJDA().getGuildCache();

                    embed.setAuthor("DocDex | Info");
                    embed.setThumbnail(EmbedUtils.ICON);
                    embed.setColor(EmbedUtils.COLOUR);
                    embed.setDescription(
                            String.format("[Website](%s) | [Github](%s) | [Invite](%s)%n%n", config.getUrl(), GITHUB, INVITE) +
                            "DocDex (Documentation Index) is a bot developed using JDA and Java 11, which can display information " +
                            "on javadoc objects, from a fuzzy query."
                    );
                    embed.addField("Creator", "[PiggyPiglet#5609](" + PIG_URL + ')', true);
                    embed.addField("Servers", formatNumber(guilds.size()) + " (" + formatNumber(guilds.stream().mapToLong(Guild::getMemberCount).sum()) + " Users)", true);
                    embed.addField("Javadocs", formatNumber(JsonParser.parseString(json).getAsJsonArray().size()) + " (Default: " + server.getDefaultJavadoc() + ')', true);
                    embed.setFooter("DocDex v" + getClass().getPackage().getImplementationVersion());

                    message.getChannel().sendMessage(embed.build()).queue();
                });
    }

    private static String formatNumber(final long number) {
        return FORMATTER.format(number);
    }
}
