package me.piggypiglet.docdex.guice.objects;

import com.google.inject.Key;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class Binding<T> {
    private final Key<? super T> key;
    private final T instance;

    public Binding(@NotNull final Key<? super T> key, @NotNull final T instance) {
        this.key = key;
        this.instance = instance;
    }

    @NotNull
    public Key<? super T> getKey() {
        return key;
    }

    @NotNull
    public T getInstance() {
        return instance;
    }
}
