package me.piggypiglet.docdex.db.orm.query;

import co.aikar.idb.Database;
import com.google.inject.Inject;
import me.piggypiglet.docdex.db.orm.query.types.ExistsQuery;
import me.piggypiglet.docdex.db.orm.query.types.SchemaQuery;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@SuppressWarnings("LanguageMismatch")
public final class QueryRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger("MySQL");

    private final Database database;

    private final SchemaQuery schema;
    private final ExistsQuery exists;

    @Inject
    public QueryRunner(@NotNull final Database database, @NotNull final SchemaQuery schema,
                       @NotNull final ExistsQuery exists) {
        this.database = database;
        this.schema = schema;
        this.exists = exists;
    }

    public boolean tableExists(@NotNull final TableStructure table) {
        return exists.generate(table).stream()
                .allMatch(query -> {
                    try {
                        return database.getResults(query).size() > 0;
                    } catch (Exception e) {
                        LOGGER.error("Something went wrong when checking if a table exists using the following query:\n" + query, e);
                    }

                    return false;
                });
    }

    public void applySchema(@NotNull final TableStructure table) {
        if (tableExists(table)) {
            LOGGER.info("Table(s) already exist for " + table.getName() + ", not applying schema(s).");
            return;
        }

        LOGGER.info("Applying schematic(s) for " + table.getName());

        schema.generate(table).forEach(query -> {
            try {
                database.executeUpdate(query);
            } catch (Exception e) {
                LOGGER.info("Something went wrong when creating the following schematic:\n" + query, e);
                System.exit(-1);
            }
        });

        LOGGER.info("Successfully applied schematic(s) for " + table.getName());
    }
}
