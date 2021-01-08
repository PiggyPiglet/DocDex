package me.piggypiglet.docdex.documentation.index.population.implementations.web.components.details.method;

import me.piggypiglet.docdex.documentation.index.population.implementations.web.components.details.DetailDeserializer;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.objects.detail.method.DocumentedMethodBuilder;
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
    private static final Pattern EXCESS_WHITESPACE = Pattern.compile("\\s\\s+");

    private MethodDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    public static DocumentedObject deserialize(@NotNull final Element method, @NotNull final String link,
                                               @NotNull final String packaj, @NotNull final String owner,
                                               final boolean old) {
        final DocumentedMethodBuilder builder = new DocumentedMethodBuilder();
        final Element details = old ? method : method.selectFirst(".detail");
        DetailDeserializer.deserialize(details, link, packaj, owner, builder, old);
        final boolean constructor = builder.getName().equalsIgnoreCase(owner);

        if (constructor) {
            builder.modifiers(builder.getReturns());
            builder.returns(builder.getName());
        }

        builder.type(constructor ? DocumentedTypes.CONSTRUCTOR : DocumentedTypes.METHOD);

        String parameters;

        if (old) {
            parameters = OldParameterDeserializer.deserialize(details, builder.getName());
        } else {
            parameters = NewParameterDeserializer.deserialize(details);
        }

        parameters = LINE_DELIMITER.matcher(parameters).replaceAll("");
        parameters = EXCESS_WHITESPACE.matcher(parameters).replaceAll(" ");
        parameters = killChars(parameters, '<', '>');
        parameters = killChars(parameters, '(', ')');

        Arrays.stream(LIST_DELIMITER.split(parameters))
                .filter(param -> !param.isBlank())
                .map(String::trim)
                .forEach(builder::parameters);

        Optional.ofNullable(details.selectFirst("dl")).ifPresent(dl -> {
            final Elements elements = dl.children();
            final Map<String, Set<String>> meta = new HashMap<>();

            Set<String> dd = new HashSet<>();
            for (final Element element : elements) {
                final String tag = element.tagName();
                final String text = element.text();

                if (tag.equalsIgnoreCase("dt")) {
                    dd = new LinkedHashSet<>();
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
                                .collect(Collectors.toMap(array -> array[0], array -> array.length > 1 ? array[1] : "",
                                        (o1, o2) -> o1, LinkedHashMap::new)));
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

    @NotNull
    private static String killChars(@NotNull final String string, final char prefix,
                                    final char suffix) {
        final StringBuilder builder = new StringBuilder();

        int count = 0;
        for (int i = 0; i < string.length(); ++i) {
            final char character = string.charAt(i);

            if (character == prefix) {
                ++count;
            }

            if (character == suffix) {
                if (--count == 0) {
                    continue;
                }
            }

            if (count != 0) {
                continue;
            }

            builder.append(character);
        }

        return builder.toString();
    }
}
