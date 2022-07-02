package me.piggypiglet.docdex.bot.embed.documentation;

import com.google.common.collect.ImmutableMap;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import me.piggypiglet.docdex.bot.embed.documentation.flexmark.CodeHtmlNodeConvertor;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.objects.detail.DetailMetadata;
import me.piggypiglet.docdex.documentation.objects.detail.method.MethodMetadata;
import me.piggypiglet.docdex.documentation.objects.type.TypeMetadata;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class SimpleObjectSerializer {
    private static final DataHolder OPTIONS = new MutableDataSet()
            .set(FlexmarkHtmlConverter.NBSP_TEXT, "\n")
            .toImmutable();
    private static final FlexmarkHtmlConverter HTML_CONVERTER = FlexmarkHtmlConverter.builder(OPTIONS)
            .htmlNodeRendererFactory(CodeHtmlNodeConvertor.Factory.create())
            .build();
    private static final int MAX_DESCRIPTION_CHARS = 950;
    private static final String ADDENDUM = "...\n\n*This description has been shortened as it was too long*.";

    private static final Map<String, Function<DocumentedObject, Object>> GETTERS = ImmutableMap.of(
            "Description:", object -> HTML_CONVERTER.convert(object.getDescription()),
            "Deprecation Message:", DocumentedObject::getDeprecationMessage,
            "Since:", DocumentedObject::getSince
    );

    private static final Map<String, Function<TypeMetadata, Integer>> TYPE_NUMBER_GETTERS =
            ImmutableMap.<String, Function<TypeMetadata, Integer>>builder()
                    .put("extensions", type -> type.getExtensions().size())
                    .put("implementations", type -> type.getImplementations().size())
                    .put("all implementations", type -> type.getAllImplementations().size())
                    .put("super interfaces", type -> type.getSuperInterfaces().size())
                    .put("sub interfaces", type -> type.getSubInterfaces().size())
                    .put("sub classes", type -> type.getSubClasses().size())
                    .put("implementing classes", type -> type.getImplementingClasses().size())
                    .put("methods", type -> type.getMethods().size())
                    .put("fields", type -> type.getFields().size())
                    .build();

    private static final Map<String, Function<MethodMetadata, String>> METHOD_GETTERS = ImmutableMap.of(
            "Returns:", MethodMetadata::getReturnsDescription,
            "Parameters:", metadata -> formatEntrySet(metadata.getParameterDescriptions().entrySet()),
            "Throws:", metadata -> formatEntrySet(metadata.getThrows())
    );

    private SimpleObjectSerializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    public static EmbedBuilder toEmbed(@NotNull final DocumentedObject object) {
        final EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription(description(object));

        final Map<String, String> values = new LinkedHashMap<>();

        GETTERS.forEach((key, getter) -> values.put(key, String.valueOf(getter.apply(object))));

        if (object.getType() == DocumentedTypes.METHOD || object.getType() == DocumentedTypes.CONSTRUCTOR) {
            METHOD_GETTERS.forEach((key, getter) -> values.put(key, getter.apply((MethodMetadata) object.getMetadata())));
        }

        values.forEach((key, value) -> {
            if (!value.isBlank() && !value.equals("null")) {
                builder.addField(key, shorten(value), false);
            }
        });

        return builder;
    }

    @NotNull
    private static String formatEntrySet(@NotNull final Set<Map.Entry<String, String>> set) {
        return set.stream()
                .filter(entry -> !entry.getValue().isBlank())
                .map(entry -> '`' + entry.getKey() + "` - " + entry.getValue())
                .collect(Collectors.joining("\n"));
    }

    @NotNull
    private static String description(@NotNull final DocumentedObject object) {
        final String signature;
        String typeNumbers = null;

        switch (object.getType()) {
            case CLASS:
            case INTERFACE:
            case ANNOTATION:
            case ENUM:
                final TypeMetadata typeMetadata = (TypeMetadata) object.getMetadata();
                final StringBuilder type = new StringBuilder()
                        .append(annotationsAndModifiers(object))
                        .append(object.getType().getCode()).append(' ')
                        .append(object.getName());

                if (!typeMetadata.getExtensions().isEmpty()) {
                    type.append("\nextends ").append(typeMetadata.getExtensions().stream()
                            .map(SimpleObjectSerializer::getAnnotationName)
                            .collect(Collectors.joining(", ")));
                }

                if (!typeMetadata.getImplementations().isEmpty()) {
                    type.append("\nimplements ").append(typeMetadata.getImplementations().stream()
                            .map(SimpleObjectSerializer::getAnnotationName)
                            .collect(Collectors.joining(", ")));
                }

                signature = type.toString();
                typeNumbers = TYPE_NUMBER_GETTERS.entrySet().stream()
                        .map(entry -> Map.entry(entry.getKey(), entry.getValue().apply(typeMetadata)))
                        .filter(entry -> entry.getValue() > 0)
                        .map(entry -> entry.getValue() + " " + entry.getKey())
                        .collect(Collectors.joining(", ", "", "."));
                break;

            case METHOD:
            case CONSTRUCTOR:
                final MethodMetadata methodMetadata = (MethodMetadata) object.getMetadata();
                final StringBuilder method = new StringBuilder().append(annotationsAndModifiers(object));

                if (object.getType() == DocumentedTypes.METHOD) {
                    method.append(methodMetadata.getReturns()).append(' ');
                }

                method.append(object.getName()).append('(')
                        .append(String.join(", ", methodMetadata.getParameters())).append(')');

                if (!methodMetadata.getThrows().isEmpty()) {
                    method.append("\nthrows ").append(methodMetadata.getThrows().stream()
                            .map(Map.Entry::getKey)
                            .map(SimpleObjectSerializer::getAnnotationName)
                            .collect(Collectors.joining(", ")));
                }

                signature = method.toString();
                break;

            case FIELD:
                signature = annotationsAndModifiers(object) +
                        ((DetailMetadata) object.getMetadata()).getReturns() + ' ' +
                        object.getName();
                break;

            default:
                return "";
        }

        if (typeNumbers != null) {
            final int i = typeNumbers.lastIndexOf(',');

            if (i != -1) {
                typeNumbers = typeNumbers.substring(0, i) + ", and " + typeNumbers.substring(i + 1);
            }

            typeNumbers = object.getName() + " has " + typeNumbers;
        }

        return "```java\n" + signature + "```" + (typeNumbers == null ? "" : '\n' + typeNumbers);
    }

    @NotNull
    private static String annotationsAndModifiers(@NotNull final DocumentedObject object) {
        return (object.getAnnotations().isEmpty() ? "" : '@' + object.getAnnotations().stream()
                .map(SimpleObjectSerializer::getAnnotationName)
                .collect(Collectors.joining(" @")) + '\n') +
                (object.getModifiers().isEmpty() ? "" : String.join(" ", object.getModifiers()) + ' ');
    }

    @NotNull
    private static String getAnnotationName(@NotNull final String fqn) {
        final int parenthesis = fqn.indexOf('(');
        return fqn.substring(fqn.substring(0, parenthesis == -1 ? fqn.length() : parenthesis).lastIndexOf('.') + 1);
    }

    @NotNull
    private static String shorten(@NotNull final String text) {
        if (text.length() < MAX_DESCRIPTION_CHARS) {
            return text;
        }

        if (text.contains("```")) {
            return shorten(text.substring(0, text.indexOf("```")).trim());
        }

        final char[] chars = text.toCharArray();
        for (int i = MAX_DESCRIPTION_CHARS; i > 0; --i) {
            if (chars[i] == ' ') {
                return text.substring(0, i).trim() + ADDENDUM;
            }
        }

        return text.substring(0, MAX_DESCRIPTION_CHARS).trim() + ADDENDUM;
    }
}
