package me.piggypiglet.docdex.db.orm.query.modification;

import me.piggypiglet.docdex.db.orm.structure.TableColumn;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class InsertQuery implements ModificationQuery {
    @NotNull
    @Override
    @Language("SQL")
    public String generate(final @NotNull TableStructure table, final @NotNull Map<String, Object> data) {
        final StringBuilder builder = new StringBuilder();

        builder.append("INSERT INTO `")
                .append(table.getName())
                .append("` (");

        builder.append(table.getColumns().stream()
                .map(TableColumn::getName)
                .map(name -> '`' + name + '`')
                .collect(Collectors.joining(", ")));

        builder.append(") VALUES (");

        builder.append(table.getColumns().stream()
                .map(TableColumn::getName)
                .map(data::get)
                .map(String::valueOf)
                .map(value -> "'" + value + "'")
                .collect(Collectors.joining(", ")));

        builder.append(");");

        return builder.toString();
    }
}
