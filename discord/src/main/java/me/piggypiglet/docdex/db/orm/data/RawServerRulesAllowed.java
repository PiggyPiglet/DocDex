package me.piggypiglet.docdex.db.orm.data;

import me.piggypiglet.docdex.db.orm.annotations.Identifier;
import me.piggypiglet.docdex.db.orm.annotations.Table;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Table("servers_rules_allowed")
public final class RawServerRulesAllowed {
    private String id;
    private String key;
    @Identifier private String allowed;

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @NotNull
    public String getAllowed() {
        return allowed;
    }
}
