package me.piggypiglet.docdex.documentation.objects;

import org.jetbrains.annotations.NotNull;

public enum Types {
    CLASS("Class"),
    INTERFACE("Interface"),
    ANNOTATION("Annotation"),
    ENUM("Enum"),

    METHOD("Method"),

    FIELD("Field"),

    PARAMETER("Parameter");

    private final String name;

    Types(@NotNull final String name) {
        this.name = name;
    }

    @NotNull
    public String getName() {
        return name;
    }
}
