package me.piggypiglet.docdex.documentation.index.population.implementations.web.components.details;

import me.piggypiglet.docdex.documentation.index.population.implementations.web.components.details.signature.NewSignatureDeserializer;
import me.piggypiglet.docdex.documentation.index.population.implementations.web.components.details.signature.OldSignatureDeserializer;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.detail.DocumentedDetailBuilder;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

import java.util.Optional;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DetailDeserializer {
    private DetailDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    public static <R extends DocumentedObject.Builder<R> & DocumentedDetailBuilder<R>> void deserialize(@NotNull final Element details, @NotNull final String link,
                                                                                                        @NotNull final String packaj, @NotNull final String owner,
                                                                                                        @NotNull final R builder, final boolean old) {
        builder
                .link(link)
                .owner(owner)
                .packaj(packaj);

        final String name;

        if (old) {
            name = details.selectFirst("h4").text();
            OldSignatureDeserializer.deserialize(details, builder, name);
        } else {
            name = details.selectFirst("h3").text();
            NewSignatureDeserializer.deserialize(details, builder);
        }

        builder.name(name);

        Optional.ofNullable(details.selectFirst(".block")).ifPresent(description -> {
            builder.description(description.html());
            builder.strippedDescription(description.text());
        });

        Optional.ofNullable(details.selectFirst(".deprecationBlock")).ifPresent(deprecationBlock -> {
            builder.deprecated(true);

            Optional.ofNullable(deprecationBlock.selectFirst(".deprecationComment")).ifPresent(deprecationComment ->
                    builder.deprecationMessage(deprecationComment.text()));
        });
    }
}
