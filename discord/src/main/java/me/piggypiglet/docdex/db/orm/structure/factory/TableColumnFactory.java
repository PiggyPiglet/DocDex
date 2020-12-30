package me.piggypiglet.docdex.db.orm.structure.factory;

import com.google.gson.FieldNamingPolicy;
import me.piggypiglet.docdex.db.orm.annotations.Length;
import me.piggypiglet.docdex.db.orm.structure.TableColumn;
import me.piggypiglet.docdex.db.orm.structure.objects.SqlDataStructures;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Optional;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TableColumnFactory {
    private TableColumnFactory() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    static TableColumn from(@NotNull final Field field) {
        final SqlDataStructures dataStructure = SqlDataStructures.fromType(field.getType())
                .orElse(null);

        if (dataStructure == null) {
            throw new AssertionError(field.getType() + " is not an implemented sql data structure.");
        }

        final int length = Optional.ofNullable(field.getAnnotation(Length.class))
                .map(Length::value)
                .orElse(dataStructure.getLength());

        return new TableColumn(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES.translateName(field), dataStructure, length);
    }
}
