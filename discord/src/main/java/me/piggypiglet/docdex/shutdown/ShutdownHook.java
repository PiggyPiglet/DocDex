package me.piggypiglet.docdex.shutdown;

import com.google.inject.Inject;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ShutdownHook extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger("JDA");

    private final JDA jda;

    @Inject
    public ShutdownHook(@NotNull final JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {
        jda.shutdownNow();
        LOGGER.info("Shut down JDA.");
    }
}
