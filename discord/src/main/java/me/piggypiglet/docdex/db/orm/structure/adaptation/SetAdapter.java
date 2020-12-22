package me.piggypiglet.docdex.db.orm.structure.adaptation;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import me.piggypiglet.docdex.db.orm.structure.factory.TableStructures;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class SetAdapter implements StructureAdapter {
    private final TableStructures tableStructures;

    @Inject
    public SetAdapter(@NotNull final TableStructures tableStructures) {
        this.tableStructures = tableStructures;
    }

    @Override
    public boolean shouldAdapt(final @NotNull Class<?> type) {
        return type.isAssignableFrom(Set.class);
    }

    @Override
    public @NotNull TableStructure serialize(final @NotNull Field field, final @NotNull String name,
                                             final @NotNull String identifier) {
        return tableStructures.from(
                TypeLiteral.get(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]).getRawType(),
                name + '_' + field.getName().toLowerCase(), field.getName()
        );
    }
}
