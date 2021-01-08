package me.piggypiglet.docdex.documentation.objects.detail.field;

import me.piggypiglet.docdex.documentation.objects.detail.DetailMetadata;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class FieldMetadata implements DetailMetadata {
    private final String owner;
    private final String returns;

    public FieldMetadata(@NotNull final String owner, @NotNull final String returns) {
        this.owner = owner;
        this.returns = returns;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final FieldMetadata that = (FieldMetadata) o;
        return owner.equals(that.owner) && returns.equals(that.returns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, returns);
    }

    @Override
    public String toString() {
        return "FieldMetadata{" +
                "owner='" + owner + '\'' +
                ", returns='" + returns + '\'' +
                '}';
    }
}
