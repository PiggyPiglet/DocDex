package me.piggypiglet.docdex.logging;

import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import org.slf4j.bridge.SLF4JBridgeHandler;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JULToSLF4JRegisterable extends Registerable {
    @Override
    public void execute() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
}
