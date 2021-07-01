package me.piggypiglet.docdex.bot.embed.documentation;

import me.piggypiglet.docdex.bot.embed.utils.EmbedUtils;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.utils.DataUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DocumentationObjectSerializer {
    private DocumentationObjectSerializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    public static EmbedBuilder toEmbed(@NotNull final User requester, @NotNull final String javadoc,
                                       @NotNull final DocumentedObject object) {
        final EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(object.isDeprecated() ? EmbedUtils.DEPRECATED_COLOUR : EmbedUtils.COLOUR);
        builder.setTitle(DataUtils.getFqn(object), object.getLink().replace(" ", "%20"));
        builder.setFooter("Requested by: " + requester.getName() + " • " + javadoc);

        return builder;
    }
}
