package me.piggypiglet.docdex.db.orm;

import com.google.inject.Inject;
import me.piggypiglet.docdex.db.orm.query.QueryRunner;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TableManager {
    private final QueryRunner queryRunner;
    private final Map<Class<?>, TableStructure> structures;

    @Inject
    public TableManager(@NotNull final QueryRunner queryRunner, @NotNull final Map<Class<?>, TableStructure> structures) {
        this.queryRunner = queryRunner;
        this.structures = structures;
    }

    public void loadTable(@NotNull final Class<?> table) {
        queryRunner.applySchema(structures.get(table));
    }
}
