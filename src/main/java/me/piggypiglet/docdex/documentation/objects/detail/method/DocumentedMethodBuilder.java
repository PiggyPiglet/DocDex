package me.piggypiglet.docdex.documentation.objects.detail.method;

import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.detail.DocumentedDetailBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.*;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DocumentedMethodBuilder extends DocumentedObject.Builder<DocumentedMethodBuilder> implements DocumentedDetailBuilder<DocumentedMethodBuilder> {
    private String owner;
    private String returns = "";
    private final Set<String> parameters = new HashSet<>();
    private final Map<String, String> parameterDescriptions = new HashMap<>();
    private String returnsDescription = "";
    private final Set<Map.Entry<String, String>> throwing = new HashSet<>();

    @NotNull
    @Override
    public DocumentedMethodBuilder owner(@NotNull final String value) {
        owner = value;
        return this;
    }

    @NotNull
    @Override
    public DocumentedMethodBuilder returns(@NotNull final String value) {
        returns = value;
        return this;
    }

    @NotNull
    public DocumentedMethodBuilder parameters(@NotNull final String @NotNull ... values) {
        Collections.addAll(parameters, values);
        return this;
    }

    @NotNull
    public DocumentedMethodBuilder parameters(@NotNull final Collection<String> values) {
        parameters.addAll(values);
        return this;
    }

    @NotNull
    public DocumentedMethodBuilder parameterDescriptions(@NotNull final String name, @NotNull final String description) {
        parameterDescriptions.put(name, description);
        return this;
    }

    @NotNull
    public DocumentedMethodBuilder parameterDescriptions(@NotNull final Map<String, String> value) {
        parameterDescriptions.putAll(value);
        return this;
    }

    @NotNull
    public DocumentedMethodBuilder returnsDescription(@NotNull final String value) {
        returnsDescription = value;
        return this;
    }

    @NotNull
    public DocumentedMethodBuilder throwing(@NotNull final String type, @NotNull final String description) {
        throwing.add(Map.entry(type, description));
        return this;
    }

    @NotNull
    public DocumentedMethodBuilder throwing(@NotNull final Set<Map.Entry<String, String>> values) {
        throwing.addAll(values);
        return this;
    }

    @NotNull
    @Override
    public DocumentedObject build() {
        return build(new MethodMetadata(
                owner, parameters, parameterDescriptions, returns, returnsDescription, throwing
        ));
    }
}
