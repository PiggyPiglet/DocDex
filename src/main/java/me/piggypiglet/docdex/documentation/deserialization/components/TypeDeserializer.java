package me.piggypiglet.docdex.documentation.deserialization.components;

import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.objects.type.DocumentedTypeBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TypeDeserializer {
    private static final Pattern LINE_DELIMITER = Pattern.compile("\n");
    private static final Pattern SPACE_DELIMITER = Pattern.compile(" ");
    private static final Pattern ANCHOR_TITLE_PACKAGE_DELIMITER = Pattern.compile(" in ");

    @SuppressWarnings("DuplicatedCode")
    @NotNull
    public static DocumentedObject deserialize(@NotNull final Element description, @Nullable final Element packaj) {
        final DocumentedTypeBuilder builder = new DocumentedTypeBuilder();

        Optional.ofNullable(packaj)
                .map(Element::text)
                .map(text -> text.replace("Package ", ""))
                .ifPresent(builder::packaj);

        final List<String> declaration = Arrays.stream(LINE_DELIMITER.split(description.selectFirst("pre").text()))
                .filter(line -> {
                    if (line.startsWith("@")) {
                        builder.annotations(line);
                        return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());


        final List<String> declarationAnchors = Optional.ofNullable(description.selectFirst("pre").selectFirst("span"))
                .map(Element::nextElementSiblings)
                .map(Collection::stream)
                .map(stream -> stream
                        .filter(element -> element.tagName().equalsIgnoreCase("a"))
                        .map(element -> ANCHOR_TITLE_PACKAGE_DELIMITER.split(element.attr("title"))[1] + '.' + element.text())
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        DocumentedTypes type = DocumentedTypes.UNKNOWN;

        int j = 0;
        for (int i = 0; i < declaration.size(); ++i) {
            final List<String> parts = Arrays.asList(SPACE_DELIMITER.split(declaration.get(i)));

            switch (i) {
                case 0:
                    type = DocumentedTypes.fromCode(parts.get(parts.size() - 2));
                    builder.type(type)
                            .name(parts.get(parts.size() - 1))
                            .modifiers(parts.subList(0, parts.size() - 2));
                    break;

                case 1:
                    if (type == DocumentedTypes.INTERFACE) {
                        for (; j < parts.size() - 1; j++) {
                            builder.extensions(declarationAnchors.get(j));
                        }
                    } else {
                        builder.extensions(declarationAnchors.get(j++));
                    }
                    break;

                case 2:
                    if (parts.isEmpty()) break;

                    for (int k = j; j < (k + parts.size()) - 1; j++) {
                        builder.extensions(declarationAnchors.get(j));
                    }
                    break;
            }
        }

        final Element descriptionBlock = description.selectFirst(".block");

        if (descriptionBlock != null) {
            builder.description(descriptionBlock.text());
        }

        description.select("dl").forEach(meta -> {
            final String header = meta.selectFirst("dt").text();
            final Set<String> items = meta.select("code > a").stream()
                    .map(anchor -> ANCHOR_TITLE_PACKAGE_DELIMITER.split(anchor.attr("title"))[1] + '.' + anchor.text())
                    .collect(Collectors.toSet());

            if (header.equalsIgnoreCase("all implemented interfaces:")) {
                builder.allImplementations(items);
            }

            if (header.equalsIgnoreCase("all superinterfaces:")) {
                builder.superInterfaces(items);
            }

            if (header.equalsIgnoreCase("all known subinterfaces:")) {
                builder.subInterfaces(items);
            }

            if (header.equalsIgnoreCase("direct known subclasses:")) {
                builder.subClasses(items);
            }

            if (header.equalsIgnoreCase("all known implementing classes:")) {
                builder.implementingClasses(items);
            }
        });

        final Element deprecationBlock = description.selectFirst(".deprecationBlock");

        if (deprecationBlock != null) {
            builder.deprecated(true);

            final Element deprecationMessage = deprecationBlock.selectFirst(".deprecationComment");

            if (deprecationMessage != null) {
                builder.deprecationMessage(deprecationMessage.text());
            }
        }

        return builder.build();
    }
}
