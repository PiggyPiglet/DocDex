package me.piggypiglet.docdex.db.orm;

import co.aikar.idb.Database;
import com.google.inject.Inject;
import me.piggypiglet.docdex.db.orm.query.QueryInitializer;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TableManager {
    private final QueryInitializer queryInitializer;
    private final Map<Class<?>, TableStructure> structures;
    private final Database database;

    @Inject
    public TableManager(@NotNull final QueryInitializer queryInitializer, @NotNull final Map<Class<?>, TableStructure> structures,
                        @NotNull final Database database) {
        this.queryInitializer = queryInitializer;
        this.structures = structures;
        this.database = database;
    }

    public void loadTable(@NotNull final Class<?> table) {
        queryInitializer.createSchema(structures.get(table)).forEach(query -> {
            try {
                System.out.println(query);
                database.executeUpdate(query);
            } catch (Exception e) {
                LoggerFactory.getLogger("MySQL").error("", e);
            }
        });
    }
}
