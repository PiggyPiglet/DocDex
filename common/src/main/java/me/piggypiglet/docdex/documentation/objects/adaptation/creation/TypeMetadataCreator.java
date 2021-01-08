package me.piggypiglet.docdex.documentation.objects.adaptation.creation;

import com.google.gson.InstanceCreator;
import me.piggypiglet.docdex.documentation.objects.type.TypeMetadata;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashSet;

// ------------------------------
// Copyright (c) PiggyPiglet 2021
// https://www.piggypiglet.me
// ------------------------------
public final class TypeMetadataCreator implements InstanceCreator<TypeMetadata> {
    @NotNull
    @Override
    public TypeMetadata createInstance(@NotNull final Type type) {
        return new TypeMetadata(new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(),
                new HashSet<>());
    }
}
