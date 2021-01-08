package me.piggypiglet.docdex.documentation.objects.adaptation.creation;

import com.google.gson.InstanceCreator;
import me.piggypiglet.docdex.documentation.objects.detail.field.FieldMetadata;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

// ------------------------------
// Copyright (c) PiggyPiglet 2021
// https://www.piggypiglet.me
// ------------------------------
public final class FieldMetadataCreator implements InstanceCreator<FieldMetadata> {
    @NotNull
    @Override
    public FieldMetadata createInstance(@NotNull final Type type) {
        return new FieldMetadata("", "");
    }
}
