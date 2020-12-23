package me.piggypiglet.docdex.db.orm.registerables;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.db.orm.TableManager;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TablesRegisterable extends Registerable {
    private final Set<Class<?>> tables;
    private final TableManager tableManager;

    @Inject
    public TablesRegisterable(@NotNull @Named("tables") final Set<Class<?>> tables, @NotNull final TableManager tableManager) {
        this.tables = tables;
        this.tableManager = tableManager;
    }

    @Override
    public void execute() {
        tables.forEach(tableManager::loadTable);
    }
}
