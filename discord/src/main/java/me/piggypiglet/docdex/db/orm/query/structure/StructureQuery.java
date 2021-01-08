package me.piggypiglet.docdex.db.orm.query.structure;

import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public interface StructureQuery {
    @NotNull
    @Language("SQL")
    String generate(@NotNull final TableStructure structure);
}
