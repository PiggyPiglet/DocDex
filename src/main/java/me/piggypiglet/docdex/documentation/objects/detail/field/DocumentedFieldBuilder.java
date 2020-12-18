package me.piggypiglet.docdex.documentation.objects.detail.field;

import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.detail.DocumentedDetailBuilder;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DocumentedFieldBuilder extends DocumentedObject.Builder<DocumentedFieldBuilder> implements DocumentedDetailBuilder<DocumentedFieldBuilder> {
    private String owner;
    private String returns = "";

    @NotNull
    @Override
    public DocumentedFieldBuilder owner(final @NotNull String value) {
        owner = value;
        return this;
    }

    @NotNull
    @Override
    public DocumentedFieldBuilder returns(final @NotNull String value) {
        returns = value;
        return this;
    }

    @NotNull
    @Override
    public DocumentedObject build() {
        return build(new FieldMetadata(owner, returns));
    }

    @NotNull
    @Override
    public String getOwner() {
        return owner;
    }

    @NotNull
    @Override
    public String getReturns() {
        return returns;
    }
}
