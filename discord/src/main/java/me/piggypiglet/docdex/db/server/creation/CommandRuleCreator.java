package me.piggypiglet.docdex.db.server.creation;

import me.piggypiglet.docdex.db.dbo.framework.DatabaseObjectCreator;
import me.piggypiglet.docdex.db.server.CommandRule;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class CommandRuleCreator implements DatabaseObjectCreator<CommandRule> {
    @NotNull
    @Override
    public CommandRule createInstance() {
        return new CommandRule(new HashSet<>(), new HashSet<>(), "This command cannot be used in this channel.");
    }
}
