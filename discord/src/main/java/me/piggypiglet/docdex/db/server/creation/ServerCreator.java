package me.piggypiglet.docdex.db.server.creation;

import com.google.gson.InstanceCreator;
import me.piggypiglet.docdex.db.server.Server;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ServerCreator implements InstanceCreator<Server> {
    @NotNull
    @Override
    public Server createInstance(final Type type) {
        return createInstance();
    }

    @NotNull
    public static Server createInstance() {
        return createInstance("");
    }

    @NotNull
    public static Server createInstance(@NotNull final String id) {
        return new Server(id, "d;", new HashSet<>(), new HashMap<>());
    }
}
