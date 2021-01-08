package me.piggypiglet.docdex.db.orm.query;

import co.aikar.idb.Database;
import com.google.inject.Inject;
import me.piggypiglet.docdex.db.orm.query.modification.DeleteQuery;
import me.piggypiglet.docdex.db.orm.query.modification.InsertQuery;
import me.piggypiglet.docdex.db.orm.query.retrieve.SelectQuery;
import me.piggypiglet.docdex.db.orm.query.structure.ExistsQuery;
import me.piggypiglet.docdex.db.orm.query.structure.SchemaQuery;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class QueryRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger("MySQL");

    private final Database database;

    private final ExistsQuery exists;
    private final SchemaQuery schema;
    private final InsertQuery insert;
    private final DeleteQuery delete;
    private final SelectQuery select;

    @Inject
    public QueryRunner(@NotNull final Database database, @NotNull final ExistsQuery exists,
                       @NotNull final SchemaQuery schema, @NotNull final InsertQuery insert,
                       @NotNull final DeleteQuery delete, @NotNull final SelectQuery select) {
        this.database = database;

        this.exists = exists;
        this.schema = schema;
        this.insert = insert;
        this.delete = delete;
        this.select = select;
    }

    public boolean exists(@NotNull final TableStructure structure) {
        @Language("SQL") final String query = exists.generate(structure);
        final String error = "Something went wrong when checking if " + structure.getName() + " exists with:\n" + query;

        return tryAndCatch(() -> database.getResults(query).size() > 0, error)
                .orElse(false);
    }

    public void applySchema(@NotNull final TableStructure table) {
        if (exists(table)) {
            LOGGER.info(table.getName() + " already exists, not applying schematic.");
            return;
        }

        @Language("SQL") final String query = schema.generate(table);
        final String error = "Something went wrong when applying " + table.getName() + "'s schema:\n" + query;

        tryAndCatch(() -> database.executeUpdate(query), error)
                .ifPresent(i -> LOGGER.info("Successfully applied schematic for " + table.getName()));
    }

    public void insert(@NotNull final TableStructure table, @NotNull final Map<String, Object> data) {
        @Language("SQL") final String query = insert.generate(table, data);
        final String error = "Something went wrong when inserting into " + table.getName() + " with the following query:\n" + query;

        tryAndCatch(() -> database.executeUpdate(query), error);
    }

    public void delete(@NotNull final TableStructure table, @NotNull final Map<String, Object> queries) {
        @Language("SQL") final String query = delete.generate(table, queries);
        final String error = "Something went wrong when deleting from " + table.getName() + " with the following query:\n" + query;

        tryAndCatch(() -> database.executeUpdate(query), error);
    }

    @NotNull
    public Set<Map<String, Object>> load(@NotNull final TableStructure table, @NotNull final Map<String, String> queries) {
        @Language("SQL") final String query = select.generate(table, queries);
        final String error = "Something went wrong when loading from " + table.getName() + " with the following query:\n" + query;

        return tryAndCatch(() -> database.getResults(query), error)
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @NotNull
    private static <T> Optional<T> tryAndCatch(@NotNull final ThrowingSupplier<T> function, @NotNull final String message) {
        try {
            return Optional.of(function.get());
        } catch (SQLException exception) {
            LOGGER.error(message, exception);
        }

        return Optional.empty();
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        @NotNull
        T get() throws SQLException;
    }
}
