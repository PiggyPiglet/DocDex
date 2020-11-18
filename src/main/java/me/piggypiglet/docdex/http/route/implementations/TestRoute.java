package me.piggypiglet.docdex.http.route.implementations;

import me.piggypiglet.docdex.http.request.Request;
import me.piggypiglet.docdex.http.route.Route;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TestRoute extends Route {
    public TestRoute() {
        super("test");
    }

    @NotNull
    @Override
    public Object respond(final @NotNull Request request) {
        return "oof";
    }
}
