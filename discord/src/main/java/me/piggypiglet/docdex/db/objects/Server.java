package me.piggypiglet.docdex.db.objects;

import me.piggypiglet.docdex.config.CommandRule;
import me.piggypiglet.docdex.db.orm.annotations.Identifier;
import me.piggypiglet.docdex.db.orm.annotations.Table;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Table(name = "servers")
public final class Server {
    @Identifier private String id;
    private String prefix;
    private Map<String, CommandRule> rules;

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(@NotNull final String prefix) {
        this.prefix = prefix;
    }

    @NotNull
    public Map<String, CommandRule> getRules() {
        return rules;
    }
}
