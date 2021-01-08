package me.piggypiglet.docdex.scanning.rules.element;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AnnotatedElement;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ElementWrapper {
    private final Class<?> type;
    private final AnnotatedElement element;

    public ElementWrapper(@NotNull final Class<?> type, @NotNull final AnnotatedElement element) {
        this.type = type;
        this.element = element;
    }

    @NotNull
    public Class<?> getType() {
        return type;
    }

    @NotNull
    public AnnotatedElement getElement() {
        return element;
    }
}
