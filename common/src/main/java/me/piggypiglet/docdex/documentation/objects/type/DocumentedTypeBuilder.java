package me.piggypiglet.docdex.documentation.objects.type;

import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.utils.DataUtils;
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
    private final Set<String> extensions = new HashSet<>();
    private final Set<String> implementations = new HashSet<>();
    private final Set<String> allImplementations = new HashSet<>();
    private final Set<String> superInterfaces = new HashSet<>();
    private final Set<String> subInterfaces = new HashSet<>();
    private final Set<String> subClasses = new HashSet<>();
    private final Set<String> implementingClasses = new HashSet<>();

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

    @NotNull
    @Override
    public DocumentedObject build() {
        return build(new TypeMetadata(extensions, implementations, allImplementations, superInterfaces,
                subInterfaces, subClasses, implementingClasses));
    }

    private static void addAll(@NotNull final Set<String> set, @NotNull final String @NotNull [] values) {
        addAll(set, Arrays.stream(values));
    }

    private static void addAll(@NotNull final Set<String> set, @NotNull final Set<String> values) {
        addAll(set, values.stream());
    }

    private static void addAll(@NotNull final Set<String> set, @NotNull final Stream<String> values) {
        values
                .map(DataUtils::removeTypeParams)
                .forEach(set::add);
    }
}
