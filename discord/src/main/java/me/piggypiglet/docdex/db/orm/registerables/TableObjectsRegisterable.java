package me.piggypiglet.docdex.db.orm.registerables;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.db.orm.annotations.Identifier;
import me.piggypiglet.docdex.db.orm.annotations.Table;
import me.piggypiglet.docdex.scanning.framework.Scanner;
import me.piggypiglet.docdex.scanning.rules.Rules;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TableObjectsRegisterable extends Registerable {
    private static final Logger LOGGER = LoggerFactory.getLogger("MySQL");
    private static final Named TABLES = Names.named("tables");

    private final Scanner scanner;

    @Inject
    public TableObjectsRegisterable(@NotNull final Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void execute(final @NotNull Injector injector) {
        final Map<Class<?>, Object> tables = scanner.getClasses(Rules.builder().hasAnnotation(Table.class).disallowMutableClasses().build())
                .filter(table -> {
                    if (Arrays.stream(table.getFields()).noneMatch(field -> field.isAnnotationPresent(Identifier.class))) {
                        LOGGER.warn(table.getName() + " is marked as a table but does not have an identification field.");
                        return false;
                    }

                    return true;
                }).collect(Collectors.toMap(table -> table, injector::getInstance));

        addBinding(new TypeLiteral<Set<Class<?>>>() {}, TABLES, tables.keySet());
        addBinding(new TypeLiteral<Map<Class<?>, Object>>() {}, TABLES, tables);
    }
}
