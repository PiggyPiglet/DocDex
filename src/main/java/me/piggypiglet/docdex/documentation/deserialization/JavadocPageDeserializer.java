package me.piggypiglet.docdex.documentation.deserialization;

import me.piggypiglet.docdex.documentation.deserialization.components.TypeDeserializer;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;

import java.util.Collections;
import java.util.Set;

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
        return Collections.singleton(TypeDeserializer.deserialize(
                document.selectFirst("main > .contentContainer > .description"),
                document.selectFirst("main > .header > .subTitle > a")
        ));
    }
}
