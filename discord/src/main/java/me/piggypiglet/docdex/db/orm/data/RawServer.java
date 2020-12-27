package me.piggypiglet.docdex.db.orm.data;

import me.piggypiglet.docdex.db.orm.annotations.Identifier;
import me.piggypiglet.docdex.db.orm.annotations.Table;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Table("servers")
public final class RawServer {
    @Identifier private String id;
    private String prefix;

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getPrefix() {
        return prefix;
    }
}
