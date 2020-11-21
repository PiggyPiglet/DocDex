package me.piggypiglet.docdex.documentation.deserialization.components.method;

import com.google.common.collect.Maps;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.objects.method.DocumentedMethodBuilder;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.stream.Collectors;

import static me.piggypiglet.docdex.documentation.deserialization.components.method.MethodDeserializer.*;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class NewMethodDeserializer {
    private NewMethodDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    static DocumentedObject deserialize(@NotNull final Element details, @NotNull final DocumentedObject owner) {
        final DocumentedMethodBuilder builder = new DocumentedMethodBuilder(owner);

        builder.type(DocumentedTypes.METHOD);
        builder.name(details.selectFirst("h3").text());

        final Element signature = details.selectFirst(".memberSignature");
        Optional.ofNullable(signature.selectFirst(".returnType")).ifPresent(returnType ->
                builder.returns(returnType.text()));
        Optional.ofNullable(signature.selectFirst(".annotations")).ifPresent(annotations ->
                builder.annotations(SPACE_DELIMITER.split(annotations.text())));
        Optional.ofNullable(signature.selectFirst(".arguments")).ifPresent(arguments ->
                builder.parameters(LIST_DELIMITER.split(LINE_DELIMITER.matcher(arguments.text()).replaceAll(" "))));

        Optional.ofNullable(details.selectFirst(".block")).ifPresent(description ->
                builder.description(description.text()));

        Optional.ofNullable(details.selectFirst("dl")).ifPresent(dl -> {
            final Elements elements = dl.children();
            final Map<String, Set<String>> meta = new HashMap<>();

            String dt = null;
            Set<String> dd = new HashSet<>();
            for (final Element element : elements) {
                final String tag = element.tagName();
                final String text = element.text();

                if (tag.equalsIgnoreCase("dt")) {
                    if (dt != null) {
                        meta.put(dt.toLowerCase(), dd);
                    }

                    dt = text;
                    dd = new HashSet<>();
                }

                if (tag.equalsIgnoreCase("dd")) {
                    dd.add(text);
                }
            }

            meta.forEach((label, content) -> {
                switch (label) {
                    case "parameters:":
                        builder.parameterDescriptions(content.stream()
                                .map(CONTENT_DELIMITER::split)
                                .collect(Collectors.toMap(array -> array[0], array -> array[1])));
                        break;

                    case "throws:":
                        builder.throwing(content.stream()
                                .map(CONTENT_DELIMITER::split)
                                .map(array -> Maps.immutableEntry(array[0], array[1]))
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
