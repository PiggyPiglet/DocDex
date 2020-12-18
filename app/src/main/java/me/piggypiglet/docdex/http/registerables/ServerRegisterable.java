package me.piggypiglet.docdex.http.registerables;

import com.google.inject.Inject;
import fi.iki.elonen.NanoHTTPD;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.http.server.HttpServer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ServerRegisterable extends Registerable {
    private static final Logger LOGGER = LoggerFactory.getLogger("HTTP");

    private final NanoHTTPD server;

    @Inject
    public ServerRegisterable(@NotNull final HttpServer server) {
        this.server = server;
    }

    @Override
    protected void execute() {
        addBinding(NanoHTTPD.class, server);

        try {
            server.start();
        } catch (IOException exception) {
            LOGGER.error("Something went wrong when starting the HTTP server.", exception);
            System.exit(1);
        }

        LOGGER.info("HTTP server started on " + server.getHostname() + ':' + server.getListeningPort());
    }
}
