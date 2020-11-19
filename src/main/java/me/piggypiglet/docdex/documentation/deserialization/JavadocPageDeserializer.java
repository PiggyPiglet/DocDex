package me.piggypiglet.docdex.documentation.deserialization;

import me.piggypiglet.docdex.documentation.deserialization.components.MethodDeserializer;
import me.piggypiglet.docdex.documentation.deserialization.components.TypeDeserializer;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JavadocPageDeserializer {
    private JavadocPageDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    public static Set<DocumentedObject> deserialize(@NotNull final Document document) {
        final DocumentedObject type = TypeDeserializer.deserialize(
                document.selectFirst(".contentContainer > .description"),
                document.selectFirst(".header > .subTitle")
        );

        final Set<DocumentedObject> methods = document.select(".methodDetails ul.blockList > li.blockList").stream()
                .map(MethodDeserializer::deserialize)
                .collect(Collectors.toSet());

        return Collections.singleton(type);
    }
}
