package me.piggypiglet.docdex.db.orm.query;

import co.aikar.idb.Database;
import com.google.inject.Inject;
import me.piggypiglet.docdex.db.orm.query.structure.ExistsQuery;
import me.piggypiglet.docdex.db.orm.query.structure.SchemaQuery;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class QueryRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger("MySQL");

    private final Database database;

    private final ExistsQuery exists;
    private final SchemaQuery schema;

    @Inject
    public QueryRunner(@NotNull final Database database, @NotNull final ExistsQuery exists,
                       @NotNull final SchemaQuery schema) {
        this.database = database;

        this.exists = exists;
        this.schema = schema;
    }

    public boolean exists(@NotNull final TableStructure structure) {
        @Language("SQL") final String query = exists.generate(structure);

        try {
            return database.getResults(query).size() > 0;
        } catch (Exception exception) {
            LOGGER.error("Something went wrong when checking if " + structure.getName() + " exists with:\n" + query, exception);
        }

        return false;
    }

    public void applySchema(@NotNull final TableStructure structure) {
        if (exists(structure)) {
            LOGGER.info(structure.getName() + " already exists, not applying schematic.");
            return;
        }

        @Language("SQL") final String query = schema.generate(structure);

        try {
            database.executeUpdate(query);
        } catch (Exception exception) {
            LOGGER.error("Something went wrong when applying " + structure.getName() + "'s schema:\n" + query, exception);
        }

        LOGGER.info("Successfully applied schematic for " + structure.getName());
    }
}
