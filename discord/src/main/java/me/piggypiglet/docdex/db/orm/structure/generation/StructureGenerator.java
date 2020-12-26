package me.piggypiglet.docdex.db.orm.structure.generation;

import com.google.inject.TypeLiteral;
import me.piggypiglet.docdex.db.orm.structure.factory.TableStructureBuilder;
import me.piggypiglet.docdex.db.orm.structure.factory.TableStructureFactory;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public interface StructureGenerator {
    Set<Class<?>> ADAPTABLE_TYPES = Set.of(
            String.class,
            Character.class, char.class,
            Byte.class, byte.class,
            Boolean.class, boolean.class,
            Short.class, short.class,
            Integer.class, int.class,
            Long.class, long.class,
            Float.class, float.class,
            Double.class, double.class
    );

    boolean shouldAdapt(@NotNull final Field field);

    @NotNull
    TableStructureBuilder generate(@NotNull final Field field, @NotNull final String name,
                                   @NotNull final TableStructureFactory structureFactory);

    static boolean checkGenericType(@NotNull final Type type) {
        final Class<?> clazz = TypeLiteral.get(type).getRawType();

        return StructureGenerator.ADAPTABLE_TYPES.stream()
                .anyMatch(clazz::equals);
    }
}
