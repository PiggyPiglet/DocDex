package me.piggypiglet.docdex.documentation.routes;

import com.google.inject.Inject;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.http.request.Request;
import me.piggypiglet.docdex.http.route.json.JsonRoute;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JavadocsRoute extends JsonRoute {
    private final Set<Javadoc> javadocs;

    @Inject
    public JavadocsRoute(@NotNull final Config config) {
        super("javadocs");
        this.javadocs = config.getJavadocs();
    }

    @NotNull
    @Override
    protected Object respond(final @NotNull Request request) {
        return javadocs;
    }
}
