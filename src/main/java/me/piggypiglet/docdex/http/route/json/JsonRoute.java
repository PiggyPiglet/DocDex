package me.piggypiglet.docdex.http.route.json;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.piggypiglet.docdex.http.request.Request;
import me.piggypiglet.docdex.http.route.Route;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class JsonRoute extends Route {
    private final Gson gson = provideGson();

    protected JsonRoute(final @NotNull String route) {
        super(route);
    }

    protected JsonRoute(final @NotNull Predicate<String> uriPattern, final @NotNull UnaryOperator<String> sanitiser) {
        super(uriPattern, sanitiser);
    }

    @NotNull
    @Override
    public String serve(@NotNull final Request request) {
        return gson.toJson(respond(request));
    }

    @NotNull
    protected Gson provideGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    @NotNull
    @Override
    public Map<String, String> getHeaders() {
        if (!super.getHeaders().containsKey("Content-Type")) {
            super.getHeaders().put("Content-Type", "application/json");
        }

        return super.getHeaders();
    }
}
