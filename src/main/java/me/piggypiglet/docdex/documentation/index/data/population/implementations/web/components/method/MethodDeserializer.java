package me.piggypiglet.docdex.documentation.index.data.population.implementations.web.components.method;

import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.objects.method.DocumentedMethodBuilder;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MethodDeserializer {
    static final Pattern LINE_DELIMITER = Pattern.compile("\n");
    static final Pattern LIST_DELIMITER = Pattern.compile(",");
    static final Pattern CONTENT_DELIMITER = Pattern.compile(" - ");

    private MethodDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    public static DocumentedObject deserialize(@NotNull final Element method, @NotNull final String packaj,
                                               @NotNull final String owner, final boolean old) {
        final Element details = Optional.ofNullable(method.selectFirst(".detail"))
                .orElse(method);
        final DocumentedMethodBuilder builder = new DocumentedMethodBuilder(owner);
        builder.packaj(packaj);

        final String name;

        if (old) {
            name = details.selectFirst("h4").text();
        } else {
            name = details.selectFirst("h3").text();
        }

        builder.type(DocumentedTypes.METHOD);
        builder.name(name);

        if (old) {
            OldSignatureDeserializer.deserialize(details, builder, name);
        } else {
            NewSignatureDeserializer.deserialize(details, builder);
        }

        Optional.ofNullable(details.selectFirst(".deprecationBlock")).ifPresent(deprecationBlock -> {
            builder.deprecated(true);

            Optional.ofNullable(deprecationBlock.selectFirst(".deprecationComment")).ifPresent(deprecationComment ->
                    builder.deprecationMessage(deprecationComment.text()));
        });

        Optional.ofNullable(details.selectFirst(".block")).ifPresent(description ->
                builder.description(description.text()));

        Optional.ofNullable(details.selectFirst("dl")).ifPresent(dl -> {
            final Elements elements = dl.children();
            final Map<String, Set<String>> meta = new HashMap<>();

            Set<String> dd = new HashSet<>();
            for (final Element element : elements) {
                final String tag = element.tagName();
                final String text = element.text();

                if (tag.equalsIgnoreCase("dt")) {
                    dd = new HashSet<>();
                    meta.put(text, dd);
                }

                if (tag.equalsIgnoreCase("dd")) {
                    dd.add(text);
                }
            }

            meta.forEach((label, content) -> {
                switch (label.toLowerCase()) {
                    case "parameters:":
                        builder.parameterDescriptions(content.stream()
                                .map(CONTENT_DELIMITER::split)
                                .collect(Collectors.toMap(array -> array[0], array -> array.length > 1 ? array[1] : "")));
                        break;

                    case "throws:":
                        builder.throwing(content.stream()
                                .map(CONTENT_DELIMITER::split)
                                .map(array -> Map.entry(array[0], array.length == 2 ? array[1] : ""))
                                .collect(Collectors.toSet()));
                        break;

                    case "returns:":
                        builder.returnsDescription(String.join("", content));
                        break;
                }
            });
        });

        return builder.build();
    }
}
