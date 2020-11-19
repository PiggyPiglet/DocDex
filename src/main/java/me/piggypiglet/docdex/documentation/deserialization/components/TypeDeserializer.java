package me.piggypiglet.docdex.documentation.deserialization.components;

import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.objects.metadata.TypeMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TypeDeserializer {
    private static final Pattern LINE_DELIMITER = Pattern.compile("\n");
    private static final Pattern SPACE_DELIMITER = Pattern.compile(" ");
    private static final Pattern COLON_DELIMITER = Pattern.compile(": ");
    private static final Pattern LIST_DELIMITER = Pattern.compile(": ");

    @SuppressWarnings("DuplicatedCode")
    @Nullable
    public static DocumentedObject deserialize(@NotNull final Element descriptionElement, @Nullable final Element packageElement) {
        final String packaj = Optional.ofNullable(packageElement).map(Element::text).orElse("");

        DocumentedTypes type = null;
        String name = null;
        String description = null;
        final Set<String> annotations = new HashSet<>();
        boolean deprecated = false;
        String deprecationMessage = null;
        final Set<String> modifiers = new HashSet<>();

        final Set<String> extensions = new HashSet<>();
        final Set<String> implementations = new HashSet<>();
        final Set<String> allImplementations = new HashSet<>();
        final Set<String> superInterfaces = new HashSet<>();
        final Set<String> subInterfaces = new HashSet<>();
        final Set<String> subClasses = new HashSet<>();
        final Set<String> implementingClasses = new HashSet<>();

        final List<String> declaration = Arrays.stream(LINE_DELIMITER.split(descriptionElement.selectFirst("pre").text()))
                .filter(line -> {
                    if (line.startsWith("@")) {
                        annotations.add(line);
                        return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());

        for (int i = 0; i < declaration.size(); ++i) {
            final List<String> parts = Arrays.asList(SPACE_DELIMITER.split(declaration.get(i)));

            switch (i) {
                case 0:
                    type = DocumentedTypes.fromName(parts.get(parts.size() - 2));
                    name = parts.get(parts.size() - 1);
                    modifiers.addAll(parts.subList(0, parts.size() - 2));
                    break;

                case 1:
                    if (type == DocumentedTypes.INTERFACE) {
                        for (final String extension : parts.subList(1, parts.size())) {
                            extensions.add(extension.replace(",", ""));
                        }
                    } else {
                        extensions.add(parts.get(1));
                    }
                    break;

                case 2:
                    for (final String implementation : parts.subList(1, parts.size())) {
                        implementations.add(implementation.replace(",", ""));
                    }
                    break;
            }
        }

        final Element descriptionBlock = descriptionElement.selectFirst(".block");

        if (descriptionBlock != null) {
            description = descriptionBlock.text();
        }

        descriptionElement.select("dl").stream()
                .map(Element::text)
                .forEach(meta -> {
                    final String[] items = LIST_DELIMITER.split(COLON_DELIMITER.split(meta)[1]);

                    if (meta.startsWith("All Implemented Interfaces:")) {
                        Collections.addAll(allImplementations, items);
                    }

                    if (meta.startsWith("All Known Superinterfaces:")) {
                        Collections.addAll(superInterfaces, items);
                    }

                    if (meta.startsWith("All Known Subinterfaces:")) {
                        Collections.addAll(subInterfaces, items);
                    }

                    if (meta.startsWith("Direct Known Subclasses:")) {
                        Collections.addAll(subClasses, items);
                    }

                    if (meta.startsWith("All Known Implementing Classes:")) {
                        Collections.addAll(implementingClasses, items);
                    }
                });

        final Element nullableDeprecationBlock = descriptionElement.selectFirst(".deprecationBlock");

        if (nullableDeprecationBlock != null) {
            deprecated = true;

            final Element nullableDeprecationMessage = nullableDeprecationBlock.selectFirst(".deprecationComment");

            if (nullableDeprecationMessage != null) {
                deprecationMessage = nullableDeprecationMessage.text();
            }
        }

        if (Stream.of(type, name, packaj).anyMatch(Objects::isNull)) {
            return null;
        }

        return new DocumentedObject(
                type, name, description, annotations, deprecated, deprecationMessage, modifiers,
                new TypeMetadata(
                        packaj, extensions, implementations, allImplementations, superInterfaces, subInterfaces,
                        subClasses, implementingClasses
                )
        );
    }
}
