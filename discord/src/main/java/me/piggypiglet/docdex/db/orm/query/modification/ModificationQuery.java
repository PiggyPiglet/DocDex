package me.piggypiglet.docdex.db.orm.query.modification;

import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public interface ModificationQuery {
    @NotNull
    @Language("SQL")
    String generate(@NotNull final TableStructure table, @NotNull final Map<String, Object> data);
}
