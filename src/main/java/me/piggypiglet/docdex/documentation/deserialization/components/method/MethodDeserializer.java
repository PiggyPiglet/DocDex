package me.piggypiglet.docdex.documentation.deserialization.components.method;

import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;

import java.util.Optional;
import java.util.regex.Pattern;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MethodDeserializer {
    static final Pattern SPACE_DELIMITER = Pattern.compile(" ");
    static final Pattern LINE_DELIMITER = Pattern.compile("\n");
    static final Pattern LIST_DELIMITER = Pattern.compile(", ");
    static final Pattern CONTENT_DELIMITER = Pattern.compile(" - ");

    private MethodDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @Nullable
    public static DocumentedObject deserialize(@NotNull final Element method, @NotNull final DocumentedObject owner) {
        return Optional.ofNullable(method.selectFirst(".detail"))
                .map(element -> NewMethodDeserializer.deserialize(method, owner))
                .orElseGet(() -> {
//                    System.out.println(owner + "#" + method.selectFirst("h3"));
                    return OldMethodDeserializer.deserialize(method);
                });
    }
}
