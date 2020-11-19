package me.piggypiglet.docdex.documentation.objects.metadata;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MethodMetadata {
    private final Set<String> parameters;
    private final Map<String, String> parameterDescriptions;
    private final String returns;
    private final String returnsDescription;
    @SerializedName("throws") private final Set<String> throwing;
    private final Set<String> throwsDescriptions;

    public MethodMetadata(@NotNull final Set<String> parameters, @NotNull final Map<String, String> parameterDescriptions,
                          @Nullable final String returns, @Nullable final String returnsDescription,
                          @NotNull final Set<String> throwing, @NotNull final Set<String> throwsDescriptions) {
        this.parameters = parameters;
        this.parameterDescriptions = parameterDescriptions;
        this.returns = returns;
        this.returnsDescription = returnsDescription;
        this.throwing = throwing;
        this.throwsDescriptions = throwsDescriptions;
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
    public Set<String> getThrows() {
        return throwing;
    }

    @NotNull
    public Set<String> getThrowsDescriptions() {
        return throwsDescriptions;
    }
}
