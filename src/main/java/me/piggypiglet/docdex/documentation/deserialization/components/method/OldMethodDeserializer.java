package me.piggypiglet.docdex.documentation.deserialization.components.method;

import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class OldMethodDeserializer {
    private OldMethodDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @Nullable
    static DocumentedObject deserialize(@NotNull final Element method) {
        return null;
    }
}
