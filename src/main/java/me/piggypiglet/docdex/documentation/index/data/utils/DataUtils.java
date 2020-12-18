package me.piggypiglet.docdex.documentation.index.data.utils;

import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.objects.detail.DetailMetadata;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DataUtils {
    private static final Set<DocumentedTypes> TYPE_TYPES = EnumSet.of(
            DocumentedTypes.CLASS, DocumentedTypes.INTERFACE,
            DocumentedTypes.ANNOTATION, DocumentedTypes.ENUM
    );

    private DataUtils() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    public static String getName(@NotNull final DocumentedObject object) {
        String prefix = object.getMetadata() instanceof DetailMetadata ? ((DetailMetadata) object.getMetadata()).getOwner() : "";

        switch (object.getType()) {
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

    public static boolean isType(@NotNull final DocumentedObject object) {
        return TYPE_TYPES.contains(object.getType());
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
