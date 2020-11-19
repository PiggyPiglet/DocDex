package me.piggypiglet.docdex.documentation.deserialization.components;

import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MethodDeserializer {
    private static final Pattern SPACE_DELIMITER = Pattern.compile(" ");
    private static final Pattern LINE_DELIMITER = Pattern.compile("\n");
    private static final Pattern LIST_DELIMITER = Pattern.compile(", ");

    @SuppressWarnings("DuplicatedCode")
    @Nullable
    public static DocumentedObject deserialize(@NotNull final Element methodElement) {
        final DocumentedTypes type = DocumentedTypes.METHOD;
        final String name = methodElement.selectFirst("h3").text();

        String description = null;
        final Set<String> annotations = new HashSet<>();
        boolean deprecated = false;
        String deprecationMessage = null;
        final Set<String> modifiers = new HashSet<>();

        final Set<String> parameters = new HashSet<>();
        String returns = null;
        final Set<String> throwing = new HashSet<>();

        final Element signature = methodElement.selectFirst(".memberSignature");
        Optional.ofNullable(signature.selectFirst(".annotations")).ifPresent(annotationsElement ->
                Collections.addAll(annotations, SPACE_DELIMITER.split(annotationsElement.text())));
        Optional.ofNullable(signature.selectFirst(".arguments")).ifPresent(argumentsElement ->
                Collections.addAll(parameters, LIST_DELIMITER.split(LINE_DELIMITER.matcher(argumentsElement.text()).replaceAll(" "))));

        return null;
    }
}
