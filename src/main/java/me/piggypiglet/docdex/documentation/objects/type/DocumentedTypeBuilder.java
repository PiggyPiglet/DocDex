package me.piggypiglet.docdex.documentation.objects.type;

import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import org.jetbrains.annotations.NotNull;

import java.util.*;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DocumentedTypeBuilder extends DocumentedObject.Builder<DocumentedTypeBuilder> {
    private static final Comparator<Object> COMPARATOR = Comparator.comparingInt(Object::hashCode);

    private String packaj = "";
    private final Set<String> extensions = new TreeSet<>(COMPARATOR);
    private final Set<String> implementations = new TreeSet<>(COMPARATOR);
    private final Set<String> allImplementations = new TreeSet<>(COMPARATOR);
    private final Set<String> superInterfaces = new TreeSet<>(COMPARATOR);
    private final Set<String> subInterfaces = new TreeSet<>(COMPARATOR);
    private final Set<String> subClasses = new TreeSet<>(COMPARATOR);
    private final Set<String> implementingClasses = new TreeSet<>(COMPARATOR);

    @NotNull
    public DocumentedTypeBuilder packaj(@NotNull final String value) {
        packaj = value;
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder extensions(@NotNull final String @NotNull ... values) {
        Collections.addAll(extensions, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder extensions(@NotNull final Collection<String> values) {
        extensions.addAll(values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder implementations(@NotNull final String @NotNull ... values) {
        Collections.addAll(implementations, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder implementations(@NotNull final Collection<String> values) {
        implementations.addAll(values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder allImplementations(@NotNull final String @NotNull ... values) {
        Collections.addAll(allImplementations, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder allImplementations(@NotNull final Collection<String> values) {
        allImplementations.addAll(values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder superInterfaces(@NotNull final String @NotNull ... values) {
        Collections.addAll(superInterfaces, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder superInterfaces(@NotNull final Collection<String> values) {
        superInterfaces.addAll(values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder subInterfaces(@NotNull final String @NotNull ... values) {
        Collections.addAll(subInterfaces, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder subInterfaces(@NotNull final Collection<String> values) {
        subInterfaces.addAll(values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder subClasses(@NotNull final String @NotNull ... values) {
        Collections.addAll(subClasses, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder subClasses(@NotNull final Collection<String> values) {
        subClasses.addAll(values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder implementingClasses(@NotNull final String @NotNull ... values) {
        Collections.addAll(implementingClasses, values);
        return this;
    }

    @NotNull
    public DocumentedTypeBuilder implementingClasses(@NotNull final Collection<String> values) {
        implementingClasses.addAll(values);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DocumentedObject build() {
        return build(new TypeMetadata(packaj, (Set) extensions, (Set) implementations, (Set) allImplementations, (Set) superInterfaces,
                (Set) subInterfaces, (Set) subClasses, (Set) implementingClasses));
    }
}
