package me.piggypiglet.docdex.documentation.objects.adaptation.creation;

import com.google.gson.InstanceCreator;
import me.piggypiglet.docdex.documentation.objects.detail.method.MethodMetadata;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;

// ------------------------------
// Copyright (c) PiggyPiglet 2021
// https://www.piggypiglet.me
// ------------------------------
public final class MethodMetadataCreator implements InstanceCreator<MethodMetadata> {
    @NotNull
    @Override
    public MethodMetadata createInstance(@NotNull final Type type) {
        return new MethodMetadata("", new HashSet<>(), new HashMap<>(), "", "", new HashSet<>());
    }
}
