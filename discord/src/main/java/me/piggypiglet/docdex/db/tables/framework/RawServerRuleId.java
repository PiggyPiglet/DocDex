package me.piggypiglet.docdex.db.tables.framework;

import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public interface RawServerRuleId extends RawServerRule {
    @NotNull
    String getId();
}
