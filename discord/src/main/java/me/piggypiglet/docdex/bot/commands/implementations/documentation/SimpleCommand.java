package me.piggypiglet.docdex.bot.commands.implementations.documentation;

import com.google.inject.Inject;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.scanning.annotations.Hidden;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
        super(Set.of("doc"), "Search a javadoc.", config);
    }

    @Override
    protected void execute(final @NotNull Message message, @NotNull final EmbedBuilder defaultEmbed,
                           final @NotNull DocumentedObject object) {
        final MessageEmbed embed = defaultEmbed.build();

        if (object.getDescription().length() > 900) {
            message.getChannel().sendMessage("This object is too big to be viewed in discord, please refer to it's javadoc page: " + object.getLink()).queue();
            return;
        }

        message.getChannel().sendMessage(embed).queue();
    }
}
