package me.piggypiglet.docdex.http.route;

import me.piggypiglet.docdex.http.request.Request;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class Route {
    private final Predicate<String> uriPattern;
    private final UnaryOperator<String> sanitiser;

    private final Map<String, String> headers = provideHeaders();

    protected Route(@NotNull final String route) {
        this(uri -> uri.startsWith(route), uri -> uri.replace(route, ""));
    }

    protected Route(@NotNull final Predicate<String> uriPattern, @NotNull final UnaryOperator<String> sanitiser) {
        this.uriPattern = uriPattern;
        this.sanitiser = sanitiser;
    }

    protected Map<String, String> provideHeaders() {
        return new HashMap<>();
    }

    @Nullable
    protected abstract Object respond(@NotNull final Request request);

    @NotNull
    public String serve(@NotNull final Request request) {
        return Optional.ofNullable(respond(request)).map(Object::toString).orElse("null");
    }

    @NotNull
    public Predicate<String> getUriPattern() {
        return uriPattern;
    }

    @NotNull
    public UnaryOperator<String> getSanitiser() {
        return sanitiser;
    }

    @NotNull
    public Map<String, String> getHeaders() {
        return headers;
    }
}
