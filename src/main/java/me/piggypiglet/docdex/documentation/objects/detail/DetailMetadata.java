package me.piggypiglet.docdex.documentation.objects.detail;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public interface DetailMetadata {
    @NotNull
    String getOwner();

    @Nullable
    String getReturns();
}
