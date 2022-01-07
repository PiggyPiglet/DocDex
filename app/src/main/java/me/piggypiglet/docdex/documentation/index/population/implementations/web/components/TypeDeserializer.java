package me.piggypiglet.docdex.documentation.index.population.implementations.web.components;

import me.piggypiglet.docdex.documentation.index.population.implementations.web.utils.DeserializationUtils;
import me.piggypiglet.docdex.documentation.index.utils.StreamUtils;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.objects.type.DocumentedTypeBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

    private TypeDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @SuppressWarnings("DuplicatedCode")
    @NotNull
    public static DocumentedObject deserialize(@NotNull final Element description, @NotNull final String link,
                                               @Nullable final Element packaj) {
        final DocumentedTypeBuilder builder = new DocumentedTypeBuilder();

        builder.link(link);
        Optional.ofNullable(packaj).ifPresent(packageElement ->
                builder.packaj(packageElement.text().replace("Package ", "")));

        final Element pre = description.selectFirst("pre, div.type-signature");
        final List<String> declaration = Arrays.stream(LINE_DELIMITER.split(pre.wholeText().trim()))
                .filter(line -> !line.startsWith("@") && !line.endsWith(")"))
                .collect(Collectors.toList());
        final Element span = pre.selectFirst("span");
        final Map<String, String> declarationFqns = Optional.ofNullable(span)
                .map(Element::nextElementSiblings)
                .map(elements -> elements.select("a"))
                .stream()
                .flatMap(Collection::stream)
                .filter(StreamUtils.distinctByKey(Element::text))
                .filter(element -> !element.parent().tagName().equalsIgnoreCase("sup"))
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
                            .map(name -> name.endsWith(",") ? name.substring(0, name.length() - 1) : name)
                            .map(name -> {
                                final int index = name.indexOf('<');

                                if (index == -1) {
                                    return declarationFqns.getOrDefault(name, name);
                                }

                                final String fqn = declarationFqns.get(name.substring(0, index));
                                return fqn == null ? name : fqn + name.substring(index);
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

        Optional.ofNullable(description.select("dl")).ifPresent(dls -> dls.forEach(dl -> {
            final Elements elements = dl.children();
            final Map<String, Set<Element>> meta = new HashMap<>();

            Set<Element> dd = new HashSet<>();
            for (final Element element : elements) {
                final String tag = element.tagName();

                if (tag.equalsIgnoreCase("dt")) {
                    dd = new LinkedHashSet<>();
                    meta.put(element.text(), dd);
                }

                if (tag.equalsIgnoreCase("dd")) {
                    dd.add(element);
                }
            }

            meta.forEach((label, ddElements) -> {
                if (label.equalsIgnoreCase("since:")) {
                    builder.since(ddElements.stream().map(Element::text).collect(Collectors.joining()));
                }

                ddElements.stream().findAny().ifPresent(element ->
                        Optional.ofNullable(HEADER_SETTERS.get(label.toLowerCase())).ifPresent(setter ->
                                setter.accept(builder, element.select("a").stream()
                                        .filter(anchor -> !anchor.parent().tagName().equalsIgnoreCase("sup"))
                                        .map(DeserializationUtils::generateFqn)
                                        .collect(Collectors.toSet()))));
            });
        }));

        Optional.ofNullable(description.selectFirst(".deprecationBlock")).ifPresent(deprecationBlock -> {
            builder.deprecated(true);

            Optional.ofNullable(deprecationBlock.selectFirst(".deprecationComment")).ifPresent(deprecationComment ->
                    builder.deprecationMessage(deprecationComment.text()));
        });

        return builder.build();
    }
}
