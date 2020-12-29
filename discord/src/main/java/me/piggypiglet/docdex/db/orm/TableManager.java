package me.piggypiglet.docdex.db.orm;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.util.Types;
import me.piggypiglet.docdex.db.orm.query.QueryRunner;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class TableManager {
    private static final Gson GSON = new Gson();
    private static final Type MAP = Types.mapOf(String.class, Object.class);

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

    public void initialize() {
        structures.values().forEach(queryRunner::applySchema);
    }

    public void loadAll(@NotNull final Class<?> clazz) {
        final Set<Object> objects = tables.get(clazz);
        final TableStructure table = structures.get(clazz);

        queryRunner.load(table, Collections.emptyMap()).stream()
                .map(GSON::toJsonTree)
                .map(json -> GSON.fromJson(json, clazz))
                .forEach(objects::add);
    }

    public void save(@NotNull final Object object) {
        queryRunner.insert(structures.get(object.getClass()), toMap(object));
    }

    public void delete(@NotNull final Object object) {
        queryRunner.delete(structures.get(object.getClass()), toMap(object));
    }

    @NotNull
    private static Map<String, Object> toMap(@NotNull final Object object) {
        return GSON.fromJson(GSON.toJsonTree(object), MAP);
    }
}
