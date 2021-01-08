package me.piggypiglet.docdex.db.orm.structure.factory;

import me.piggypiglet.docdex.db.orm.structure.TableColumn;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TableStructureFactory {
    private TableStructureFactory() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    public static TableStructure from(@NotNull final Class<?> clazz, @NotNull final String name,
                                      @NotNull final Set<Field> identifiers) {
        final Set<Field> fields = Set.of(clazz.getDeclaredFields());
        final Set<TableColumn> columns = fields.stream()
                .map(TableColumnFactory::from)
                .collect(Collectors.toSet());

        return new TableStructure(clazz, name, identifiers.stream().map(TableColumnFactory::from).collect(Collectors.toSet()), columns);
    }
}
