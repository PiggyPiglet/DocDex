package me.piggypiglet.docdex.documentation.routes;

import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import fi.iki.elonen.NanoHTTPD;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.index.DocumentationIndex;
import me.piggypiglet.docdex.http.request.Request;
import me.piggypiglet.docdex.http.route.exceptions.StatusCodeException;
import me.piggypiglet.docdex.http.route.json.JsonRoute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class IndexRoute extends JsonRoute {
    private final DocumentationIndex index;
    private final Map<String, Javadoc> javadocs;
    private final Set<CompletableFuture<?>> startupHooks;

    @Inject
    public IndexRoute(@NotNull final DocumentationIndex index, @NotNull final Config config,
                      @NotNull @Named("startup") final Set<CompletableFuture<?>> startupHooks) {
        super("index");
        this.index = index;
        this.javadocs = new HashMap<>();
        this.startupHooks = startupHooks;

        config.getJavadocs().forEach(javadoc -> javadoc.getNames().forEach(name -> javadocs.put(name, javadoc)));
    }

    @Nullable
    @Override
    protected Object respond(@NotNull final Request request) {
        if (!startupHooks.isEmpty()) {
            throw new StatusCodeException(NanoHTTPD.Response.Status.SERVICE_UNAVAILABLE, "DocDex is still initializing.");
        }

        final Multimap<String, String> params = request.getParams();
        final String javadocName = params.get("javadoc").stream().findAny().orElse(null);
        final String query = params.get("query").stream().findAny()
                .map(str -> str.replace("~", "#"))
                .map(str -> str.replace("-", "%"))
                .orElse(null);
        final int limit = params.get("limit").stream().findAny().map(Integer::parseInt).orElse(5);

        if (javadocName == null || query == null) {
            return null;
        }

        final Javadoc javadoc = javadocs.get(javadocName);

        if (javadoc == null) {
            return null;
        }

        return index.get(javadoc, query.replace("%20", " "), limit);
    }
}
