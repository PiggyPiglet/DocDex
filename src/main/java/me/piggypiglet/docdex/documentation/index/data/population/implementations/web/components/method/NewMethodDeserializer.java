package me.piggypiglet.docdex.documentation.index.data.population.implementations.web.components.method;

import me.piggypiglet.docdex.documentation.index.data.population.implementations.web.utils.DeserializationUtils;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.objects.method.DocumentedMethodBuilder;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.stream.Collectors;

import static me.piggypiglet.docdex.documentation.index.data.population.implementations.web.components.method.MethodDeserializer.*;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class NewMethodDeserializer {
    private NewMethodDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    static DocumentedObject deserialize(@NotNull final Element details, @NotNull final String packaj,
                                        @NotNull final String owner) {
        final DocumentedMethodBuilder builder = new DocumentedMethodBuilder(packaj, owner);

        builder.type(DocumentedTypes.METHOD);
        builder.name(details.selectFirst("h3").text());

        final Element signature = details.selectFirst(".memberSignature");
        Optional.ofNullable(signature.selectFirst(".annotations"))
                .map(annotations -> annotations.select("a").stream())
                .map(annotations -> annotations.map(element -> element.text(element.text().substring(1))))
                .map(annotations -> annotations.map(DeserializationUtils::generateFqn))
                .ifPresent(annotations -> annotations.forEach(annotation -> builder.annotations('@' + annotation)));
        Optional.ofNullable(signature.selectFirst(".returnType")).ifPresent(returnType ->
                builder.returns(returnType.text()));
        Optional.ofNullable(signature.selectFirst(".arguments")).ifPresent(arguments ->
                builder.parameters(LIST_DELIMITER.split(LINE_DELIMITER.matcher(arguments.text()).replaceAll(" "))));

        Optional.ofNullable(details.selectFirst(".deprecationBlock")).ifPresent(deprecationBlock -> {
            builder.deprecated(true);

            Optional.ofNullable(deprecationBlock.selectFirst(".deprecationComment")).ifPresent(deprecationComment -> {
                builder.deprecationMessage(deprecationComment.text());
            });
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
                                .collect(Collectors.toMap(array -> array[0], array -> array[1])));
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
