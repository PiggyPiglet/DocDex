package me.piggypiglet.docdex.documentation.routes;

import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.index.DocumentationIndex;
import me.piggypiglet.docdex.http.request.Request;
import me.piggypiglet.docdex.http.route.json.JsonRoute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class IndexRoute extends JsonRoute {
    private final DocumentationIndex index;
    private final Map<String, Javadoc> javadocs;

    @Inject
    public IndexRoute(@NotNull final DocumentationIndex index, @NotNull final Config config) {
        super("index");
        this.index = index;
        this.javadocs = new HashMap<>();

        config.getJavadocs().forEach(javadoc -> javadoc.getNames().forEach(name -> javadocs.put(name, javadoc)));
    }

    @Nullable
    @Override
    protected Object respond(@NotNull final Request request) {
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
