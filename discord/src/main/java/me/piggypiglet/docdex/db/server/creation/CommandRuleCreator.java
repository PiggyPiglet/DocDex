package me.piggypiglet.docdex.db.server.creation;

import com.google.gson.InstanceCreator;
import me.piggypiglet.docdex.db.server.CommandRule;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashSet;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class CommandRuleCreator implements InstanceCreator<CommandRule> {
    @NotNull
    @Override
    public CommandRule createInstance(final Type type) {
        return createInstance();
    }

    @NotNull
    public static CommandRule createInstance() {
        return new CommandRule(new HashSet<>(), new HashSet<>(), "This command cannot be used in this channel.");
    }
}
