package me.piggypiglet.docdex.http.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import fi.iki.elonen.NanoHTTPD;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.http.request.Request;
import me.piggypiglet.docdex.http.route.Route;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class HttpServer extends NanoHTTPD {
    private final Set<Route> routes;

    @Inject
    public HttpServer(@NotNull final Config config, @NotNull @Named("routes") final Set<Route> routes) {
        super(config.getHost(), config.getPort());
        this.routes = routes;

        setAsyncRunner(new DefaultAsyncRunner());
    }

    @NotNull
    @Override
    public Response serve(@NotNull final IHTTPSession session) {
        final String uri = session.getUri().toLowerCase().substring(1);

        for (final Route route : routes) {
            if (!route.getUriPattern().test(uri)) continue;

            final String sanitisedUri = route.getSanitiser().apply(uri);

            if (!sanitisedUri.isEmpty() && !sanitisedUri.equals("/") && !sanitisedUri.startsWith("?")) continue;

            final Response response = newFixedLengthResponse(route.serve(Request.from(session)));
            route.getHeaders().forEach(response::addHeader);

            return response;
        }

        return newFixedLengthResponse("You probably want to go to /index.");
    }
}
