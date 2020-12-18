package me.piggypiglet.docdex.documentation.objects.detail;

import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public interface DocumentedDetailBuilder<R extends DocumentedObject.Builder<R> & DocumentedDetailBuilder<R>> {
    @NotNull
    R owner(@NotNull final String value);

    @NotNull
    R returns(@NotNull final String value);
}
