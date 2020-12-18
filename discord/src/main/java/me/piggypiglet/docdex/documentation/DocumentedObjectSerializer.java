package me.piggypiglet.docdex.documentation;

import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.detail.DetailMetadata;
import me.piggypiglet.docdex.documentation.objects.detail.method.MethodMetadata;
import me.piggypiglet.docdex.documentation.objects.type.TypeMetadata;
import me.piggypiglet.docdex.documentation.utils.DataUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DocumentedObjectSerializer {
    private static final Map<String, Function<DocumentedObject, Object>> GETTERS = Map.of(
            "Description:", DocumentedObject::getDescription,
            "Deprecation Message:", DocumentedObject::getDeprecationMessage
    );

    private DocumentedObjectSerializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    public static MessageEmbed toEmbed(@NotNull final String javadoc, @NotNull final DocumentedObject object) {
        final EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(DataUtils.getFqn(object));
        builder.setAuthor(javadoc);
        builder.setDescription(generateSignature(object));

        GETTERS.forEach((key, getter) -> {
            final String value = String.valueOf(getter.apply(object));

            if (!value.isBlank() || !value.equalsIgnoreCase("null")) {
                builder.addField(key, value, false);
            }
        });

        return builder.build();
    }

    @NotNull
    private static String generateSignature(@NotNull final DocumentedObject object) {
        switch (object.getType()) {
            case CLASS:
            case INTERFACE:
            case ANNOTATION:
            case ENUM:
                return annotationsAndModifiers(object) +
                        object.getType().getCode() + ' ' +
                        object.getName();

            case METHOD:
                return annotationsAndModifiers(object) +
                        ((DetailMetadata) object.getMetadata()).getReturns() + ' ' +
                        object.getName() + '(' +
                        String.join(", ", ((MethodMetadata) object.getMetadata()).getParameters()) + ')';

            case CONSTRUCTOR:
                return annotationsAndModifiers(object) +
                        object.getName() + '(' +
                        String.join(", ", ((MethodMetadata) object.getMetadata()).getParameters()) + ')';

            case FIELD:
                return annotationsAndModifiers(object) +
                        ((DetailMetadata) object.getMetadata()).getReturns() + ' ' +
                        object.getName();
        }

        return "";
    }

    @NotNull
    private static String annotationsAndModifiers(@NotNull final DocumentedObject object) {
        return (object.getAnnotations().isEmpty() ? "" : String.join(" ", object.getAnnotations()) + '\n') +
                (object.getModifiers().isEmpty() ? "" : String.join(" ", object.getModifiers()) + ' ');
    }
}
