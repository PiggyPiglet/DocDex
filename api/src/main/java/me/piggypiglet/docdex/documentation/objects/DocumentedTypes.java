package me.piggypiglet.docdex.documentation.objects;

import org.jetbrains.annotations.NotNull;

public enum DocumentedTypes {
    CLASS("Class"),
    INTERFACE("Interface"),
    ANNOTATION("Annotation", "@interface"),
    ENUM("Enum"),

    METHOD("Method"),
    CONSTRUCTOR("Constructor"),

    FIELD("Field"),

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
}
