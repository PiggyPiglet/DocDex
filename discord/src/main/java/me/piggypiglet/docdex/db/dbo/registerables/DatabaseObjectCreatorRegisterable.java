package me.piggypiglet.docdex.db.dbo.registerables;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.db.dbo.framework.DatabaseObjectCreator;
import me.piggypiglet.docdex.scanning.framework.Scanner;
import me.piggypiglet.docdex.scanning.rules.Rules;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DatabaseObjectCreatorRegisterable extends Registerable {
    private static final Named CREATORS = Names.named("creators");
    private static final Named DEFAULT = Names.named("default");

    private final Scanner scanner;

    @Inject
    public DatabaseObjectCreatorRegisterable(@NotNull final Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void execute(@NotNull final Injector injector) {
        final Map<Class<?>, DatabaseObjectCreator<?>> creators =
                scanner.getClasses(Rules.builder().typeExtends(DatabaseObjectCreator.class).disallowMutableClasses().build())
                        .map(injector::getInstance)
                        .map(creator -> (DatabaseObjectCreator<?>) creator)
                        .collect(Collectors.toMap(DatabaseObjectCreatorRegisterable::getType, creator -> creator));

        addBinding(new TypeLiteral<Map<Class<?>, DatabaseObjectCreator<?>>>() {}, CREATORS, creators);
        creators.forEach((clazz, creator) -> bindDefault(clazz, creator.createInstance()));
    }

    @SuppressWarnings("unchecked")
    private <T> void bindDefault(@NotNull final Class<?> clazz, @NotNull final Object object) {
        addBinding((Class<T>) clazz, DEFAULT, (T) object);
    }

    @Nullable
    private static Class<?> getType(@NotNull final DatabaseObjectCreator<?> creator) {
        return Arrays.stream(creator.getClass().getGenericInterfaces())
                .filter(i -> TypeLiteral.get(i).getRawType() == DatabaseObjectCreator.class)
                .map(ParameterizedType.class::cast)
                .findAny()
                .map(parameterizedType -> TypeLiteral.get(parameterizedType.getActualTypeArguments()[0]).getRawType())
                .orElse(null);

    }
}
