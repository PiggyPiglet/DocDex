package me.piggypiglet.docdex.db.objects;

import me.piggypiglet.docdex.config.CommandRule;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class Server {
    private final String id;
    private String prefix;
    private final Map<String, CommandRule> rules;

    public Server(@NotNull final String id, @NotNull final String prefix,
                  @NotNull final Map<String, CommandRule> rules) {
        this.id = id;
        this.prefix = prefix;
        this.rules = rules;
    }

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
