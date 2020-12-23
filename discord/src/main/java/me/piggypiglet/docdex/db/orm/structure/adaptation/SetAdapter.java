package me.piggypiglet.docdex.db.orm.structure.adaptation;

import com.google.inject.TypeLiteral;
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
public final class SetAdapter implements StructureAdapter {
    @Override
    public boolean shouldAdapt(final @NotNull Field field) {
        return field.getType().isAssignableFrom(Set.class);
    }

    @Override
    public @NotNull TableStructureBuilder generate(final @NotNull Field field, final @NotNull String name,
                                                   @NotNull final TableStructureFactory structureFactory) {
        final String identifier = field.getName();
        final String tableName = name + '_' + identifier.toLowerCase();
        final Class<?> type = TypeLiteral.get(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]).getRawType();

        if (StructureAdapter.checkGenericType(type)) {
            return TableStructureBuilder.builder(false)
                    .name(tableName)
                    .identifier(identifier)
                    .columns(identifier);
        }

        return structureFactory.builder(type, name + '_' + field.getName().toLowerCase(), field.getName());
    }
}
