package me.piggypiglet.docdex.db.orm.query;

import com.google.inject.Inject;
import me.piggypiglet.docdex.db.orm.query.types.SchemaQuery;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class QueryInitializer {
    private final SchemaQuery schemaQuery;

    @Inject
    public QueryInitializer(@NotNull final SchemaQuery schemaQuery) {
        this.schemaQuery = schemaQuery;
    }

    @NotNull
    public Set<String> createSchema(@NotNull final TableStructure structure) {
        return schemaQuery.generate(structure);
    }
}
