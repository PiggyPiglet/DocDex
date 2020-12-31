package me.piggypiglet.docdex.bot.commands.implementations.documentation;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bot.embed.documentation.SimpleObjectSerializer;
import me.piggypiglet.docdex.bot.embed.utils.EmbedUtils;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.scanning.annotations.Hidden;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Hidden
public final class SimpleCommand extends DocumentationCommand {
    @Inject
    public SimpleCommand(@NotNull final Config config) {
        super(Set.of("search"), "Search a javadoc.", config);
    }

    @Override
    protected void execute(final @NotNull Message message, @NotNull final EmbedBuilder defaultEmbed,
                           final @NotNull DocumentedObject object) {
        final EmbedBuilder embed = SimpleObjectSerializer.toEmbed(object);
        EmbedUtils.merge(defaultEmbed, embed);
        message.getChannel().sendMessage(embed.build()).queue();
    }

    @NotNull
    @Override
    protected String checkAndReturnError(final @NotNull DocumentedObject object) {
        if (object.getDescription().length() > 1024) {
            return "This object is too big to be viewed in discord, please refer to it's javadoc page: " + object.getLink();
        }

        return super.checkAndReturnError(object);
    }
}
