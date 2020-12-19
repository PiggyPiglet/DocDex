package me.piggypiglet.docdex.bot.commands.implementations.documentation;

import com.google.inject.Inject;
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
        super(Set.of("doc"), "search a javadoc", config.getUrl(), config.getDefaultJavadoc());
    }

    @Override
    protected void execute(final @NotNull Message message, @NotNull final EmbedBuilder defaultEmbed,
                           final @NotNull DocumentedObject object) {
        message.getChannel().sendMessage(defaultEmbed.build()).queue();
    }
}
