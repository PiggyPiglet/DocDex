package me.piggypiglet.docdex.db.orm.data;

import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class QueryInitializer {
    @NotNull
    public Map<String, Object> load(@NotNull final TableStructure structure) {
        return Collections.emptyMap();
    }
}
