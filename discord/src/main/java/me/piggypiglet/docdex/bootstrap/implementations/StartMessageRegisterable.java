package me.piggypiglet.docdex.bootstrap.implementations;

import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class StartMessageRegisterable extends Registerable {
    private static final Logger LOGGER = LoggerFactory.getLogger("DocDex");

    @Override
    public void execute() {
        LOGGER.info("DocDex Bot initialization process complete.");
    }
}
