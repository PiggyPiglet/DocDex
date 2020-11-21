package me.piggypiglet.docdex.documentation.objects.type;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.serialization.MethodSetSerializer;
import me.piggypiglet.docdex.documentation.objects.serialization.TypeSetSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TypeMetadata {
    @SerializedName("package") private final String packaj;
    @JsonAdapter(TypeSetSerializer.class) private final Set<DocumentedObject> extensions;
    @JsonAdapter(TypeSetSerializer.class) private final Set<DocumentedObject> implementations;
    @JsonAdapter(TypeSetSerializer.class) private final Set<DocumentedObject> allImplementations;
    @JsonAdapter(TypeSetSerializer.class) private final Set<DocumentedObject> superInterfaces;
    @JsonAdapter(TypeSetSerializer.class) private final Set<DocumentedObject> subInterfaces;
    @JsonAdapter(TypeSetSerializer.class) private final Set<DocumentedObject> subClasses;
    @JsonAdapter(TypeSetSerializer.class) private final Set<DocumentedObject> implementingClasses;
    @JsonAdapter(MethodSetSerializer.class) private final Set<DocumentedObject> methods = new HashSet<>();

    public TypeMetadata(@NotNull final String packaj, @NotNull final Set<DocumentedObject> extensions,
                        @NotNull final Set<DocumentedObject> implementations, @NotNull final Set<DocumentedObject> allImplementations,
                        @NotNull final Set<DocumentedObject> superInterfaces, @NotNull final Set<DocumentedObject> subInterfaces,
                        @NotNull final Set<DocumentedObject> subClasses, @NotNull final Set<DocumentedObject> implementingClasses) {
        this.packaj = packaj;
        this.extensions = extensions;
        this.implementations = implementations;
        this.allImplementations = allImplementations;
        this.superInterfaces = superInterfaces;
        this.subInterfaces = subInterfaces;
        this.subClasses = subClasses;
        this.implementingClasses = implementingClasses;
    }

    @NotNull
    public String getPackage() {
        return packaj;
    }

    @NotNull
    public Set<DocumentedObject> getExtensions() {
        return extensions;
    }

    @NotNull
    public Set<DocumentedObject> getImplementations() {
        return implementations;
    }

    @NotNull
    public Set<DocumentedObject> getAllImplementations() {
        return allImplementations;
    }

    @NotNull
    public Set<DocumentedObject> getSuperInterfaces() {
        return superInterfaces;
    }

    @NotNull
    public Set<DocumentedObject> getSubInterfaces() {
        return subInterfaces;
    }

    @NotNull
    public Set<DocumentedObject> getSubClasses() {
        return subClasses;
    }

    @NotNull
    public Set<DocumentedObject> getImplementingClasses() {
        return implementingClasses;
    }

    @NotNull
    public Set<DocumentedObject> getMethods() {
        return methods;
    }
}
