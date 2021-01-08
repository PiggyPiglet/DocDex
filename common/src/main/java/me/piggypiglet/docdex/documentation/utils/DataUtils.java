package me.piggypiglet.docdex.documentation.utils;

import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.MongoDocumentedObjectFields;
import me.piggypiglet.docdex.documentation.objects.detail.DetailMetadata;
import me.piggypiglet.docdex.documentation.objects.detail.method.MethodMetadata;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DataUtils {
    private static final Pattern SPACE_DELIMITER = Pattern.compile("[\u00a0 ]");

    private DataUtils() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    public static String getName(@NotNull final DocumentedObject object) {
        String prefix = object.getMetadata() instanceof DetailMetadata ? ((DetailMetadata) object.getMetadata()).getOwner() : "";

        switch (object.getType()) {
            case CONSTRUCTOR:
            case METHOD:
                prefix += '#';
                break;
            case FIELD:
                prefix += '%';
                break;
        }

        return prefix + object.getName();
    }

    @NotNull
    public static String getFqn(@NotNull final DocumentedObject object) {
        return object.getPackage() + '.' + getName(object);
    }

    @NotNull
    public static Map<ParameterTypes, String> getParams(@NotNull final DocumentedObject object) {
        final List<String[]> parameters = ((MethodMetadata) object.getMetadata()).getParameters().stream()
                .map(SPACE_DELIMITER::split)
                .collect(Collectors.toList());
        final Map<ParameterTypes, String> result = new EnumMap<>(ParameterTypes.class);

        Stream.of(
                Map.entry(ParameterTypes.FULL, List.of(-2, -1)),
                Map.entry(ParameterTypes.TYPE, List.of(-1)),
                Map.entry(ParameterTypes.NAME, List.of(-2))
        ).forEach(paramEntry ->
                result.put(paramEntry.getKey(), parameters.stream()
                        .map(param -> paramEntry.getValue().stream()
                                .map(offset -> param[param.length + offset])
                                .collect(Collectors.joining(" ")))
                        .collect(Collectors.joining(", ")).toLowerCase()
                )
        );

        return result;
    }

    @NotNull
    public static MongoDocumentedObjectFields fromParameterType(@NotNull final ParameterTypes parameter, final boolean fqn) {
        switch (parameter) {
            case FULL:
                return fqn ? MongoDocumentedObjectFields.IDENTIFIER : MongoDocumentedObjectFields.FULL_PARAMS;

            case TYPE:
                return fqn ? MongoDocumentedObjectFields.FQN_TYPE_PARAMS : MongoDocumentedObjectFields.TYPE_PARAMS;

            case NAME:
                return fqn ? MongoDocumentedObjectFields.FQN_NAME_PARAMS : MongoDocumentedObjectFields.NAME_PARAMS;
        }

        throw new AssertionError("Something went extremely impossibly wrong");
    }

    @NotNull
    public static String getName(@NotNull final Javadoc javadoc) {
        return String.join("-", javadoc.getNames());
    }

    @NotNull
    public static String removeTypeParams(@NotNull final String type) {
        int lastIndex = type.lastIndexOf('<');
        lastIndex = lastIndex == -1 ? type.length() : lastIndex;

        return type.substring(0, lastIndex);
    }
}
