package me.piggypiglet.docdex.db.adapters.registerables;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.util.Types;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.db.adapters.DatabaseObjectAdapter;
import me.piggypiglet.docdex.scanning.framework.Scanner;
import me.piggypiglet.docdex.scanning.rules.Rules;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DatabaseObjectRegisterable extends Registerable {
    private static final Named ADAPTERS = Names.named("adapters");

    private final Scanner scanner;

    @Inject
    public DatabaseObjectRegisterable(@NotNull final Scanner scanner) {
        this.scanner = scanner;
    }

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "unchecked"})
    @Override
    public void execute(final @NotNull Injector injector) {
        final Set<DatabaseObjectAdapter<?>> adapters = scanner.getClasses(Rules.builder().typeExtends(DatabaseObjectAdapter.class).disallowMutableClasses().build())
                .map(injector::getInstance)
                .map(adapter -> (DatabaseObjectAdapter<?>) adapter)
                .collect(Collectors.toSet());

        addBinding(new TypeLiteral<Set<DatabaseObjectAdapter<?>>>() {}, ADAPTERS, adapters);

        final Map<DatabaseObjectAdapter<?>, Set<?>> adapterSets = new HashMap<>();
        final Map<Class<?>, DatabaseObjectAdapter<?>> adapterMap = new HashMap<>();

        adapters.forEach(adapter -> {
            final Type type = ((ParameterizedType) Arrays.stream(adapter.getClass().getGenericInterfaces())
                    .filter(i -> TypeLiteral.get(i).getRawType() == DatabaseObjectAdapter.class)
                    .findAny().get()).getActualTypeArguments()[0];

            final Set<?> set = adapter.loadFromRaw();
            adapterSets.put(adapter, set);
            adapterMap.put(TypeLiteral.get(type).getRawType(), adapter);
            addBinding((Key<Set<?>>) Key.get(Types.setOf(type)), set);
        });

        addBinding(new TypeLiteral<Map<DatabaseObjectAdapter<?>, Set<?>>>() {}, ADAPTERS, adapterSets);
        addBinding(new TypeLiteral<Map<Class<?>, DatabaseObjectAdapter<?>>>() {}, ADAPTERS, adapterMap);
    }
}
