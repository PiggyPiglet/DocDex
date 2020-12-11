package me.piggypiglet.docdex.documentation.objects.type;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.serialization.MethodSetSerializer;
import me.piggypiglet.docdex.documentation.objects.serialization.TypeSetSerializer;
import me.piggypiglet.docdex.documentation.objects.util.PotentialObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TypeMetadata {
    @SerializedName("package") private final String packaj;
    @JsonAdapter(TypeSetSerializer.class) private final Set<PotentialObject> extensions;
    @JsonAdapter(TypeSetSerializer.class) private final Set<PotentialObject> implementations;
    @JsonAdapter(TypeSetSerializer.class) private final Set<PotentialObject> allImplementations;
    @JsonAdapter(TypeSetSerializer.class) private final Set<PotentialObject> superInterfaces;
    @JsonAdapter(TypeSetSerializer.class) private final Set<PotentialObject> subInterfaces;
    @JsonAdapter(TypeSetSerializer.class) private final Set<PotentialObject> subClasses;
    @JsonAdapter(TypeSetSerializer.class) private final Set<PotentialObject> implementingClasses;
    @JsonAdapter(MethodSetSerializer.class) private final Set<DocumentedObject> methods = new HashSet<>();

    public TypeMetadata(@NotNull final String packaj, @NotNull final Set<PotentialObject> extensions,
                        @NotNull final Set<PotentialObject> implementations, @NotNull final Set<PotentialObject> allImplementations,
                        @NotNull final Set<PotentialObject> superInterfaces, @NotNull final Set<PotentialObject> subInterfaces,
                        @NotNull final Set<PotentialObject> subClasses, @NotNull final Set<PotentialObject> implementingClasses) {
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
    public Set<PotentialObject> getExtensions() {
        return extensions;
    }

    @NotNull
    public Set<PotentialObject> getImplementations() {
        return implementations;
    }

    @NotNull
    public Set<PotentialObject> getAllImplementations() {
        return allImplementations;
    }

    @NotNull
    public Set<PotentialObject> getSuperInterfaces() {
        return superInterfaces;
    }

    @NotNull
    public Set<PotentialObject> getSubInterfaces() {
        return subInterfaces;
    }

    @NotNull
    public Set<PotentialObject> getSubClasses() {
        return subClasses;
    }

    @NotNull
    public Set<PotentialObject> getImplementingClasses() {
        return implementingClasses;
    }

    @NotNull
    public Set<DocumentedObject> getMethods() {
        return methods;
    }
}
