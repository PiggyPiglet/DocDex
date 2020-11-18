package me.piggypiglet.docdex.shutdown;

import com.google.inject.Inject;
import fi.iki.elonen.NanoHTTPD;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ShutdownHook extends Thread {
    private static final Logger HTTP_LOGGER = LoggerFactory.getLogger("HTTP");

    private final NanoHTTPD server;

    @Inject
    public ShutdownHook(@NotNull final NanoHTTPD server) {
        this.server = server;
    }

    @Override
    public void run() {
        server.stop();
        HTTP_LOGGER.info("Shutting down HTTP server.");
    }
}
