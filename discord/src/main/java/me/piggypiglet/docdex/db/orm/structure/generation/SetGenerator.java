package me.piggypiglet.docdex.db.orm.structure.generation;

import com.google.inject.TypeLiteral;
import me.piggypiglet.docdex.db.orm.structure.TableField;
import me.piggypiglet.docdex.db.orm.structure.factory.TableStructureBuilder;
import me.piggypiglet.docdex.db.orm.structure.factory.TableStructureFactory;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class SetGenerator implements StructureGenerator {
    @Override
    public boolean shouldAdapt(final @NotNull Field field) {
        return field.getType().isAssignableFrom(Set.class);
    }

    @Override
    public @NotNull TableStructureBuilder generate(final @NotNull Field field, final @NotNull String name,
                                                   @NotNull final TableStructureFactory structureFactory) {
        final TableField identifier = TableField.of(field);
        final String tableName = name + '_' + field.getName().toLowerCase();
        final Class<?> type = TypeLiteral.get(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]).getRawType();

        if (StructureGenerator.checkGenericType(type)) {
            return TableStructureBuilder.builder(false)
                    .clazz(type)
                    .name(tableName)
                    .identifier(identifier)
                    .columns(identifier);
        }

        return structureFactory.builder(type, name + '_' + field.getName().toLowerCase(), identifier);
    }
}
