package me.piggypiglet.docdex.documentation.index.data.utils;

import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.objects.method.MethodMetadata;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DataUtils {
    private DataUtils() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    public static String getName(@NotNull final DocumentedObject object) {
        final String prefix;

        if (object.getType() == DocumentedTypes.METHOD) {
            prefix = ((MethodMetadata) object.getMetadata()).getOwner() + '#';
        } else {
            prefix = "";
        }

        return prefix + object.getName();
    }

    @NotNull
    public static String getFqn(@NotNull final DocumentedObject object) {
        return object.getPackage() + '.' + getName(object);
    }
}
