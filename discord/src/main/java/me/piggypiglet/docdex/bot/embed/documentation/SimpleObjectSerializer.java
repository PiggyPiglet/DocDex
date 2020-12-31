package me.piggypiglet.docdex.bot.embed.documentation;

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
    private static final Map<String, Function<DocumentedObject, Object>> GETTERS = Map.of(
            "Description:", DocumentedObject::getDescription,
            "Deprecation Message:", DocumentedObject::getDeprecationMessage
    );

    private static final Map<String, Function<MethodMetadata, String>> METHOD_GETTERS = Map.of(
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
        builder.setDescription("```java\n" + generateSignature(object) + "```");

        final Map<String, String> values = new LinkedHashMap<>();

        GETTERS.forEach((key, getter) -> values.put(key, String.valueOf(getter.apply(object))));

        if (object.getType() == DocumentedTypes.METHOD || object.getType() == DocumentedTypes.CONSTRUCTOR) {
            METHOD_GETTERS.forEach((key, getter) -> values.put(key, getter.apply((MethodMetadata) object.getMetadata())));
        }

        values.forEach((key, value) -> {
            if (!value.isBlank() && !value.equals("null")) {
                builder.addField(key, value, false);
            }
        });

        return builder;
    }

    @NotNull
    private static String formatEntrySet(@NotNull final Set<Map.Entry<String, String>> set) {
        return set.stream()
                .filter(entry -> !entry.getValue().isBlank())
                .map(entry -> entry.getKey() + " - " + entry.getValue())
                .collect(Collectors.joining("\n"));
    }

    @NotNull
    private static String generateSignature(@NotNull final DocumentedObject object) {
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
                            .map(SimpleObjectSerializer::getName)
                            .collect(Collectors.joining(", ")));
                }

                if (!typeMetadata.getImplementations().isEmpty()) {
                    type.append("\nimplements ").append(typeMetadata.getImplementations().stream()
                            .map(SimpleObjectSerializer::getName)
                            .collect(Collectors.joining(", ")));
                }

                return type.toString();

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
                            .map(SimpleObjectSerializer::getName)
                            .collect(Collectors.joining(", ")));
                }

                return method.toString();

            case FIELD:
                return annotationsAndModifiers(object) +
                        ((DetailMetadata) object.getMetadata()).getReturns() + ' ' +
                        object.getName();
        }

        return "";
    }

    @NotNull
    private static String annotationsAndModifiers(@NotNull final DocumentedObject object) {
        return (object.getAnnotations().isEmpty() ? "" : '@' + object.getAnnotations().stream().map(SimpleObjectSerializer::getName).collect(Collectors.joining(", @")) + '\n') +
                (object.getModifiers().isEmpty() ? "" : String.join(" ", object.getModifiers()) + ' ');
    }

    @NotNull
    private static String getName(@NotNull final String fqn) {
        return fqn.substring(fqn.lastIndexOf('.') + 1);
    }
}
