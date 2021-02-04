package me.piggypiglet.docdex.documentation.response;

import me.piggypiglet.docdex.documentation.objects.DocumentedObjectResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ObjectListResult {
    private final List<DocumentedObjectResult> results;
    private final String error;

    public ObjectListResult(@Nullable final List<DocumentedObjectResult> results, @NotNull final String error) {
        this.results = results;
        this.error = error;
    }

    @Nullable
    public List<DocumentedObjectResult> getResults() {
        return results;
    }

    @NotNull
    public String getError() {
        return error;
    }
}
