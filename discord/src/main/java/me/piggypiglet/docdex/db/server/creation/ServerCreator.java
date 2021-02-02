package me.piggypiglet.docdex.db.server.creation;

import com.google.inject.Inject;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.db.dbo.framework.DatabaseObjectCreator;
import me.piggypiglet.docdex.db.server.Server;
import me.piggypiglet.docdex.documentation.index.algorithm.Algorithm;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ServerCreator implements DatabaseObjectCreator<Server> {
    private final Config config;

    @Inject
    public ServerCreator(@NotNull final Config config) {
        this.config = config;
    }

    @Override
    public @NotNull Server createInstance() {
        return createInstance("");
    }

    @NotNull
    public Server createInstance(@NotNull final String id) {
        return new Server(id, config.getPrefix(), Algorithm.DUKE_JARO_WINKLER, config.getDefaultJavadoc(), new HashSet<>(), new HashMap<>(), new HashSet<>());
    }
}
