package me.piggypiglet.docdex.db.orm.structure.registerables;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.db.orm.annotations.Identifier;
import me.piggypiglet.docdex.db.orm.annotations.Table;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import me.piggypiglet.docdex.db.orm.structure.factory.TableStructureFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class StructuresRegisterable extends Registerable {
    private final Set<Class<?>> tables;
    private final TableStructureFactory structureFactory;

    @Inject
    public StructuresRegisterable(@NotNull @Named("tables") final Set<Class<?>> tables, @NotNull final TableStructureFactory structureFactory) {
        this.tables = tables;
        this.structureFactory = structureFactory;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public void execute() {
        addBinding(new TypeLiteral<Map<Class<?>, TableStructure>>() {}, tables.stream()
                .collect(Collectors.toMap(table -> table, table -> {
                    final String name = table.getAnnotation(Table.class).name();
                    final String identifier = Arrays.stream(table.getDeclaredFields())
                            .filter(field -> field.isAnnotationPresent(Identifier.class))
                            .findAny()
                            .get()
                            .getName();

                    return structureFactory.from(table, name, identifier);
                })));
    }
}
