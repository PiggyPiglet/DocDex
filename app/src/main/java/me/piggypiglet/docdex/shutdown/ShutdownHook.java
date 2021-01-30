package me.piggypiglet.docdex.shutdown;

import com.google.inject.Inject;
import com.mongodb.MongoClient;
import fi.iki.elonen.NanoHTTPD;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ShutdownHook extends Thread {
    private static final Logger SHUTDOWN_SPACER = LoggerFactory.getLogger("Shutdown Spacer");

    private final NanoHTTPD server;
    private final MongoClient client;

    @Inject
    public ShutdownHook(@NotNull final NanoHTTPD server, @NotNull final MongoClient client) {
        this.server = server;
        this.client = client;
    }

    @Override
    public void run() {
        server.stop();
        LoggerFactory.getLogger("HTTP").info("Shut down HTTP server.");

        client.close();
        LoggerFactory.getLogger("Mongo").info("Shut down MongoDB connection.");

        for (int i = 0; i < 5; ++i) {
            SHUTDOWN_SPACER.info("");
        }
    }
}
