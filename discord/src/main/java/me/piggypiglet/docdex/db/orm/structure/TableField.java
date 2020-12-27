package me.piggypiglet.docdex.db.orm.structure;

import me.piggypiglet.docdex.db.orm.query.SqlDataTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Objects;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TableField {
    private final String string;
    private final Field field;
    private final int level;

    private final String name;
    private final SqlDataTypes type;

    private TableField(@Nullable final String string, @Nullable final Field field,
                       final int level) {
        this.string = string;
        this.field = field;
        this.level = level;

        this.name = isField() ? Objects.requireNonNull(field).getName().toLowerCase() : string;
        this.type = isField() ? SqlDataTypes.from(Objects.requireNonNull(field).getType()).orElse(SqlDataTypes.TEXT) : SqlDataTypes.TEXT;
    }

    @NotNull
    public static TableField of(@NotNull final String field) {
        return new TableField(field, null, 0);
    }

    @NotNull
    public static TableField of(@NotNull final String field, final int level) {
        return new TableField(field, null, level);
    }

    @NotNull
    public static TableField of(@NotNull final Field field) {
        return new TableField(null, field, 0);
    }

    @NotNull
    public static TableField of(@NotNull final Field field, final int level) {
        return new TableField(null, field, level);
    }

    @Nullable
    public String getString() {
        return string;
    }

    @Nullable
    public Field getField() {
        return field;
    }

    public int getLevel() {
        return level;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public SqlDataTypes getType() {
        return type;
    }

    public boolean isField() {
        return string == null;
    }

    public boolean isString() {
        return field == null;
    }

    @Override
    public String toString() {
        return level + ":" + (isString() ? string : field.getName());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TableField that = (TableField) o;
        return level == that.level && Objects.equals(string, that.string) && Objects.equals(field, that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(string, field, level);
    }
}
