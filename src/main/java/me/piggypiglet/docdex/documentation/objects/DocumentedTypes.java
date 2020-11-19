package me.piggypiglet.docdex.documentation.objects;

import org.jetbrains.annotations.NotNull;

public enum DocumentedTypes {
    CLASS("Class"),
    INTERFACE("Interface"),
    ANNOTATION("Annotation", "@interface"),
    ENUM("Enum"),

    METHOD("Method"),

    FIELD("Field"),

    PARAMETER("Parameter"),

    UNKNOWN("Unknown");

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
    public static DocumentedTypes fromName(@NotNull final String name) {
        for (final DocumentedTypes type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }

        return UNKNOWN;
    }
}
