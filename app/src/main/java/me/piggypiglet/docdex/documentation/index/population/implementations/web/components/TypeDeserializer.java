package me.piggypiglet.docdex.documentation.index.population.implementations.web.components;

import me.piggypiglet.docdex.documentation.index.population.implementations.web.utils.DeserializationUtils;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.objects.type.DocumentedTypeBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TypeDeserializer {
    private static final Pattern LINE_DELIMITER = Pattern.compile("\n");
    private static final Pattern SPACE_DELIMITER = Pattern.compile(" ");
    private static final Pattern ANNOTATION_DELIMITER = Pattern.compile("@");

    private static final Map<String, BiConsumer<DocumentedTypeBuilder, Set<String>>> HEADER_SETTERS = Map.of(
            "all implemented interfaces:", DocumentedTypeBuilder::allImplementations,
            "all superinterfaces:", DocumentedTypeBuilder::superInterfaces,
            "all known subinterfaces:", DocumentedTypeBuilder::subInterfaces,
            "direct known subclasses:", DocumentedTypeBuilder::subClasses,
            "all known implementing classes:", DocumentedTypeBuilder::implementingClasses
    );

    @SuppressWarnings("DuplicatedCode")
    @NotNull
    public static DocumentedObject deserialize(@NotNull final Element description, @NotNull final String link,
                                               @Nullable final Element packaj) {
        final DocumentedTypeBuilder builder = new DocumentedTypeBuilder();

        builder.link(link);
        Optional.ofNullable(packaj).ifPresent(packageElement ->
                builder.packaj(packageElement.text().replace("Package ", "")));

        final Element pre = description.selectFirst("pre");
        final List<String> declaration = Arrays.stream(LINE_DELIMITER.split(pre.text()))
                .filter(line -> !line.startsWith("@") && !line.endsWith(")"))
                .collect(Collectors.toList());
        final Element span = pre.selectFirst("span");
        final Map<String, String> declarationFqns = Optional.ofNullable(span)
                .map(Element::nextElementSiblings)
                .map(elements -> elements.select("a"))
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Element::text, DeserializationUtils::generateFqn));

        for (int i = 0; i < declaration.size(); ++i) {
            final List<String> parts = Arrays.asList(SPACE_DELIMITER.split(declaration.get(i)));

            switch (i) {
                case 0:
                    int nameIndex = parts.size() - 2;
                    for (int k = 0; k < parts.size(); ++k) {
                        final String part = parts.get(k);

                        if (k == parts.size() - 1) {
                            builder.name(part);
                            break;
                        }

                        if (part.contains("<")) {
                            builder.name(part.substring(0, part.indexOf('<')));
                            nameIndex = k;
                            break;
                        }
                    }

                    builder.type(DocumentedTypes.fromCode(parts.get(parts.size() - 2)))
                            .modifiers(parts.subList(0, nameIndex));
                    break;

                case 1:
                case 2:
                    final Consumer<String> setter = i == 1 ? builder::extensions : builder::implementations;
                    parts.subList(1, parts.size()).stream()
                            .map(name -> {
                                final int index = name.indexOf('<');

                                if (index == -1) {
                                    return declarationFqns.get(name);
                                }

                                final String fqn = declarationFqns.get(name.substring(0, index));
                                return fqn + name.substring(index);
                            })
                            .forEach(setter);
                    break;
            }
        }

        final Set<Element> annotationAnchors = Optional.ofNullable(span)
                .map(Element::previousElementSiblings)
                .map(elements -> elements.select("a"))
                .stream()
                .flatMap(Collection::stream)
                .filter(element -> element.attr("title").startsWith("annotation in "))
                .collect(Collectors.toSet());

        final StringBuilder annotations = new StringBuilder();

        for (final String line : LINE_DELIMITER.split(pre.text())) {
            if (line.endsWith(builder.getName())) {
                break;
            }

            annotations.append(line);
        }

        for (final String annotation : ANNOTATION_DELIMITER.split(annotations.toString())) {
            final Element anchor = annotationAnchors.stream()
                    .filter(element -> annotation.startsWith(element.text().substring(1)))
                    .findAny().orElse(null);

            if (anchor == null) {
                continue;
            }

            final String fqnAnnotation = ('@' + annotation.replaceFirst(anchor.text(), DeserializationUtils.generateFqn(anchor)));
            builder.annotations(DeserializationUtils.removeExcessWhitespace(fqnAnnotation).trim());
        }

        Optional.ofNullable(description.selectFirst(".block")).ifPresent(descriptionBlock -> {
            builder.description(descriptionBlock.html());
            builder.strippedDescription(descriptionBlock.text());
        });

        description.select("dl").forEach(meta -> {
            final String header = meta.selectFirst("dt").text().toLowerCase();

            Optional.ofNullable(HEADER_SETTERS.get(header)).ifPresent(setter ->
                    setter.accept(builder, meta.select("dd a").stream()
                            .map(DeserializationUtils::generateFqn)
                            .collect(Collectors.toSet())));
        });

        Optional.ofNullable(description.selectFirst(".deprecationBlock")).ifPresent(deprecationBlock -> {
            builder.deprecated(true);

            Optional.ofNullable(deprecationBlock.selectFirst(".deprecationComment")).ifPresent(deprecationComment ->
                    builder.deprecationMessage(deprecationComment.text()));
        });

        return builder.build();
    }
}
