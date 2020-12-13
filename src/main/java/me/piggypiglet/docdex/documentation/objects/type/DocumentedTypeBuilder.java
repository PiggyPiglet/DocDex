package me.piggypiglet.docdex.documentation.objects.type;

import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.util.PotentialObject;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DocumentedTypeBuilder extends DocumentedObject.Builder<DocumentedTypeBuilder> {
    private String packaj = "";
    private final Set<PotentialObject> extensions = new HashSet<>();
    private final Set<PotentialObject> implementations = new HashSet<>();
    private final Set<PotentialObject> allImplementations = new HashSet<>();
    private final Set<PotentialObject> superInterfaces = new HashSet<>();
    private final Set<PotentialObject> subInterfaces = new HashSet<>();
    private final Set<PotentialObject> subClasses = new HashSet<>();
    private final Set<PotentialObject> implementingClasses = new HashSet<>();

    @NotNull
    public DocumentedTypeBuilder packaj(@NotNull final String value) {
        packaj = value;
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder extensions(@NotNull final String @NotNull ... values) {
        addAll(extensions, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder extensions(@NotNull final Set<String> values) {
        addAll(extensions, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder implementations(@NotNull final String @NotNull ... values) {
        addAll(implementations, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder implementations(@NotNull final Set<String> values) {
        addAll(implementations, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder allImplementations(@NotNull final String @NotNull ... values) {
        addAll(allImplementations, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder allImplementations(@NotNull final Set<String> values) {
        addAll(allImplementations, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder superInterfaces(@NotNull final String @NotNull ... values) {
        addAll(superInterfaces, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder superInterfaces(@NotNull final Set<String> values) {
        addAll(superInterfaces, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder subInterfaces(@NotNull final String @NotNull ... values) {
        addAll(subInterfaces, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder subInterfaces(@NotNull final Set<String> values) {
        addAll(subInterfaces, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder subClasses(@NotNull final String @NotNull ... values) {
        addAll(subClasses, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder subClasses(@NotNull final Set<String> values) {
        addAll(subClasses, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder implementingClasses(@NotNull final String @NotNull ... values) {
        addAll(implementingClasses, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder implementingClasses(@NotNull final Set<String> values) {
        addAll(implementingClasses, values);
        return this;
    }
    
    @Override
    public DocumentedObject build() {
        return build(new TypeMetadata(packaj, extensions, implementations, allImplementations, superInterfaces,
                subInterfaces, subClasses, implementingClasses));
    }

    private static void addAll(@NotNull final Set<PotentialObject> set, @NotNull final String @NotNull [] values) {
        addAll(set, Arrays.stream(values));
    }

    private static void addAll(@NotNull final Set<PotentialObject> set, @NotNull final Set<String> values) {
        addAll(set, values.stream());
    }

    private static void addAll(@NotNull final Set<PotentialObject> set, @NotNull final Stream<String> values) {
        values
                .map(PotentialObject::of)
                .forEach(set::add);
    }
}
