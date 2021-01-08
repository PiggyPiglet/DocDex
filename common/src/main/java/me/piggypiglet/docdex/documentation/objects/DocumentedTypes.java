package me.piggypiglet.docdex.documentation.objects;

import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

public enum DocumentedTypes {
    CLASS("Class", "class"),
    INTERFACE("Interface", "interface"),
    ANNOTATION("Annotation", "@interface"),
    ENUM("Enum", "enum"),

    METHOD("Method"),
    CONSTRUCTOR("Constructor"),

    FIELD("Field"),

    UNKNOWN("Unknown");

    private static final Set<DocumentedTypes> TYPES = EnumSet.of(CLASS, INTERFACE, ANNOTATION, ENUM);
    private static final Set<DocumentedTypes> METHODS = EnumSet.of(METHOD, CONSTRUCTOR);

    private final String name;
    private final String code;

    DocumentedTypes(@NotNull final String name) {
        this(name, name);
    }

    DocumentedTypes(@NotNull final String name, @NotNull final String code) {
        this.name = name;
        this.code = code;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getCode() {
        return code;
    }

    @NotNull
    public static DocumentedTypes fromCode(@NotNull final String code) {
        for (final DocumentedTypes type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }

        return UNKNOWN;
    }

    public static boolean isType(@NotNull final DocumentedTypes type) {
        return TYPES.contains(type);
    }

    public static boolean isMethod(@NotNull final DocumentedTypes type) {
        return METHODS.contains(type);
    }

    public static boolean isField(@NotNull final DocumentedTypes type) {
        return type == FIELD;
    }
}
