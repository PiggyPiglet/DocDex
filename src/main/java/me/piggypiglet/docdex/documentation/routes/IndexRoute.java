package me.piggypiglet.docdex.documentation.routes;

import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import me.piggypiglet.docdex.documentation.index.DocumentationIndex;
import me.piggypiglet.docdex.http.request.Request;
import me.piggypiglet.docdex.http.route.json.JsonRoute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class IndexRoute extends JsonRoute {
    private final DocumentationIndex index;

    @Inject
    public IndexRoute(@NotNull final DocumentationIndex index) {
        super("index");
        this.index = index;
    }

    @Nullable
    @Override
    protected Object respond(@NotNull final Request request) {
        final Multimap<String, String> params = request.getParams();
        final String javadoc = params.get("javadoc").stream().findAny().orElse(null);
        final String query = params.get("query").stream().findAny().orElse(null);

        if (javadoc == null || query == null) {
            return null;
        }

        return index.get(javadoc, query);
    }
}
