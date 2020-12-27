package me.piggypiglet.docdex.db.orm.query.structure;

import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ExistsQuery implements StructureQuery {
    @NotNull
    @Override
    @Language("SQL")
    public String generate(final @NotNull TableStructure structure) {
        return "SHOW TABLES LIKE '" + structure.getName() + "';";
    }
}
