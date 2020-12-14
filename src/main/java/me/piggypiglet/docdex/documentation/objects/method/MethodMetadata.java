package me.piggypiglet.docdex.documentation.objects.method;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import me.piggypiglet.docdex.documentation.objects.adaptation.EntrySetAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MethodMetadata {
    @SerializedName("package") private final String packaj;
    private final String owner;
    private final Set<String> parameters;
    private final Map<String, String> parameterDescriptions;
    private final String returns;
    private final String returnsDescription;
    @SerializedName("throws") @JsonAdapter(EntrySetAdapter.class) private final Set<Map.Entry<String, String>> throwing;

    MethodMetadata(@NotNull final String packaj, @NotNull final String owner,
                   @NotNull final Set<String> parameters, @NotNull final Map<String, String> parameterDescriptions,
                   @Nullable final String returns, @Nullable final String returnsDescription,
                   @NotNull final Set<Map.Entry<String, String>> throwing) {
        this.packaj = packaj;
        this.owner = owner;
        this.parameters = parameters;
        this.parameterDescriptions = parameterDescriptions;
        this.returns = returns;
        this.returnsDescription = returnsDescription;
        this.throwing = throwing;
    }

    @NotNull
    public String getPackage() {
        return packaj;
    }

    @NotNull
    public String getOwner() {
        return owner;
    }

    @NotNull
    public Set<String> getParameters() {
        return parameters;
    }

    @NotNull
    public Map<String, String> getParameterDescriptions() {
        return parameterDescriptions;
    }

    @Nullable
    public String getReturns() {
        return returns;
    }

    @Nullable
    public String getReturnsDescription() {
        return returnsDescription;
    }

    @NotNull
    public Set<Map.Entry<String, String>> getThrows() {
        return throwing;
    }

    @Override
    public String toString() {
        return "MethodMetadata{" +
                "package='" + packaj + '\'' +
                ", owner='" + owner + '\'' +
                ", parameters=" + parameters +
                ", parameterDescriptions=" + parameterDescriptions +
                ", returns='" + returns + '\'' +
                ", returnsDescription='" + returnsDescription + '\'' +
                ", throws=" + throwing +
                '}';
    }
}
