package me.piggypiglet.docdex.db.orm.structure.registerables;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.db.orm.annotations.Identifier;
import me.piggypiglet.docdex.db.orm.annotations.Table;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import me.piggypiglet.docdex.db.orm.structure.factory.TableStructureFactory;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TableStructuresRegisterable extends Registerable {
    private static final Named TABLES = Names.named("tables");

    private final Set<Class<?>> tables;

    @Inject
    public TableStructuresRegisterable(@NotNull @Named("tables") final Set<Class<?>> tables) {
        this.tables = tables;
    }

    @Override
    public void execute() {
        final Map<Class<?>, TableStructure> structures = tables.stream()
                .collect(Collectors.toMap(clazz -> clazz, clazz -> {
                    final String name = clazz.getAnnotation(Table.class).value();
                    final Set<Field> identifiers = Arrays.stream(clazz.getDeclaredFields())
                            .filter(field -> field.isAnnotationPresent(Identifier.class))
                            .collect(Collectors.toSet());

                    return TableStructureFactory.from(clazz, name, identifiers);
                }));

        addBinding(new TypeLiteral<Map<Class<?>, TableStructure>>() {}, TABLES, structures);
    }
}
