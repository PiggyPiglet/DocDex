package me.piggypiglet.docdex.documentation.index.population.implementations.web.components;

import me.piggypiglet.docdex.documentation.index.population.implementations.web.utils.DeserializationUtils;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.objects.type.DocumentedTypeBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
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
        final List<String> declarationAnchors = Optional.ofNullable(span)
                .map(Element::nextElementSiblings)
                .map(elements -> elements.select("a"))
                .stream()
                .flatMap(Collection::stream)
                .map(DeserializationUtils::generateFqn)
                .collect(Collectors.toList());

        DocumentedTypes type = DocumentedTypes.UNKNOWN;

        int j = 0;
        final AtomicInteger extensionCount = new AtomicInteger(0);
        for (int i = 0; i < declaration.size(); ++i) {
            final List<String> parts = Arrays.asList(SPACE_DELIMITER.split(declaration.get(i)));

            switch (i) {
                case 0:
                    type = DocumentedTypes.fromCode(parts.get(parts.size() - 2));

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

                    builder.type(type)
                            .modifiers(parts.subList(0, nameIndex));
                    break;

                case 1:
                    final List<String> extensions = parts.subList(1, parts.size());
                    final Function<Integer, String> extension = k ->
                            declarationAnchors.size() > k ? declarationAnchors.get(k) : extensions.get(k);

                    if (type == DocumentedTypes.INTERFACE) {
                        for (; j < parts.size() - 1; j++) {
                            builder.extensions(extension.apply(j));
                            extensionCount.incrementAndGet();
                        }
                    } else {
                        builder.extensions(extension.apply(j++));
                        extensionCount.incrementAndGet();
                    }
                    break;

                case 2:
                    final List<String> implementations = parts.subList(1, parts.size());
                    final Function<Integer, String> implementation = k ->
                            declarationAnchors.size() > k ? declarationAnchors.get(k) : implementations.get(k - extensionCount.get());

                    for (int k = j; j < (k + parts.size()) - 1; j++) {
                        builder.implementations(implementation.apply(j));
                    }
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

        Optional.ofNullable(description.selectFirst(".block")).ifPresent(descriptionBlock ->
                builder.description(descriptionBlock.text()));

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
