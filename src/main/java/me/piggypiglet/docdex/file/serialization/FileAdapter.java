package me.piggypiglet.docdex.file.serialization;

import com.google.inject.ImplementedBy;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@ImplementedBy(JsonAdapter.class)
public interface FileAdapter {
    @NotNull
    Map<String, Object> fromString(@NotNull final String content);
}
