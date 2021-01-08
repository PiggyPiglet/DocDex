package me.piggypiglet.docdex.bootstrap.implementations;

import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class StartupHookRegisterable extends Registerable {
    private static final Named STARTUP = Names.named("startup");

    @Override
    public void execute() {
        addBinding(long.class, STARTUP, System.currentTimeMillis());
        addBinding(new TypeLiteral<Set<CompletableFuture<?>>>() {}, STARTUP, new HashSet<>());
    }
}
