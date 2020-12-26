package me.piggypiglet.docdex.db.orm.structure.generation;

import com.google.inject.TypeLiteral;
import me.piggypiglet.docdex.db.orm.structure.TableField;
import me.piggypiglet.docdex.db.orm.structure.factory.TableStructureBuilder;
import me.piggypiglet.docdex.db.orm.structure.factory.TableStructureFactory;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MapGenerator implements StructureGenerator {
    @Override
    public boolean shouldAdapt(final @NotNull Field field) {
        return field.getType().isAssignableFrom(Map.class) &&
                StructureGenerator.checkGenericType(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
    }

    @Override
    public @NotNull TableStructureBuilder generate(final @NotNull Field field, final @NotNull String name,
                                                   @NotNull final TableStructureFactory structureFactory) {
        final Type[] params = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
        final Class<?> value = TypeLiteral.get(params[1]).getRawType();
        final String identifier;
        final boolean intermediate;

        if (Arrays.stream(value.getDeclaredFields()).map(Field::getGenericType).anyMatch(type -> !StructureGenerator.checkGenericType(type))) {
            identifier = "key";
            intermediate = false;
        } else {
            identifier = "";
            intermediate = true;
        }

        final TableStructureBuilder builder =
                structureFactory.builder(value, name + '_' + field.getName().toLowerCase(), TableField.of(identifier), intermediate);

        if (!identifier.isBlank()) {
            builder.columns(TableField.of(identifier));
        }

        return builder;
    }
}
