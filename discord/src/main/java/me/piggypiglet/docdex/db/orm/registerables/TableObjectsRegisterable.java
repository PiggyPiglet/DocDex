package me.piggypiglet.docdex.db.orm.registerables;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.util.Types;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.db.orm.annotations.Identifier;
import me.piggypiglet.docdex.db.orm.annotations.Table;
import me.piggypiglet.docdex.scanning.framework.Scanner;
import me.piggypiglet.docdex.scanning.rules.Rules;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TableObjectsRegisterable extends Registerable {
    private static final Named TABLES = Names.named("tables");

    private final Scanner scanner;

    @Inject
    public TableObjectsRegisterable(@NotNull final Scanner scanner) {
        this.scanner = scanner;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(final @NotNull Injector injector) {
        final Set<Class<?>> tableClasses = scanner.getClasses(Rules.builder().hasAnnotation(Table.class).disallowMutableClasses().build())
                .filter(clazz -> Arrays.stream(clazz.getDeclaredFields()).anyMatch(field -> field.isAnnotationPresent(Identifier.class)))
                .collect(Collectors.toUnmodifiableSet());

        addBinding(new TypeLiteral<Set<Class<?>>>() {}, TABLES, tableClasses);

        final Map<Class<?>, Set<Object>> tables = tableClasses.stream()
                .collect(Collectors.toMap(table -> table, table -> new HashSet<>()));

        addBinding(new TypeLiteral<Map<Class<?>, Set<Object>>>() {}, TABLES, tables);
        tables.forEach((clazz, set) -> addBinding((Key<Set<?>>) Key.get(Types.setOf(clazz)), set));
    }
}
