package me.piggypiglet.docdex.db.orm;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.db.orm.query.QueryRunner;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class TableManager {
    private final QueryRunner queryRunner;
    private final Map<Class<?>, Set<Object>> tables;
    private final Map<Class<?>, TableStructure> structures;

    @Inject
    public TableManager(@NotNull final QueryRunner queryRunner, @NotNull @Named("tables") final Map<Class<?>, Set<Object>> tables,
                        @NotNull @Named("tables") final Map<Class<?>, TableStructure> structures) {
        this.queryRunner = queryRunner;
        this.tables = tables;
        this.structures = structures;
    }

    public void load(@NotNull final Class<?> clazz) {
        queryRunner.applySchema(structures.get(clazz));
    }
}
