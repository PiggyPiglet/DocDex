package me.piggypiglet.docdex.documentation.index.population.implementations.web.components.details.field;

import me.piggypiglet.docdex.documentation.index.population.implementations.web.components.details.DetailDeserializer;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.objects.detail.field.DocumentedFieldBuilder;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class FieldDeserializer {
    private FieldDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    public static DocumentedObject deserialize(@NotNull final Element method, @NotNull final String link,
                                               @NotNull final String packaj, @NotNull final String owner,
                                               final boolean old) {
        final DocumentedFieldBuilder builder = new DocumentedFieldBuilder()
                .type(DocumentedTypes.FIELD);

        DetailDeserializer.deserialize(method, link, packaj, owner, builder, old);

        return builder.build();
    }
}
