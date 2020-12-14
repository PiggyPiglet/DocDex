package me.piggypiglet.docdex.shutdown;

import com.google.inject.Inject;
import com.mongodb.MongoClient;
import fi.iki.elonen.NanoHTTPD;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ShutdownHook extends Thread {
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
        LoggerFactory.getLogger("HTTP").info("Shutting down HTTP server.");

        client.close();
        LoggerFactory.getLogger("Mongo").info("Shutting down Mongo connection.");
    }
}
