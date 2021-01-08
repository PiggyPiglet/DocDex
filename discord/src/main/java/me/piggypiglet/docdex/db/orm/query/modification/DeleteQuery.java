package me.piggypiglet.docdex.db.orm.query.modification;

import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import me.piggypiglet.docdex.db.utils.MysqlUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DeleteQuery implements ModificationQuery {
    @NotNull
    @Override
    public String generate(final @NotNull TableStructure table, final @NotNull Map<String, Object> data) {
        final StringBuilder builder = new StringBuilder();

        builder.append("DELETE FROM ")
                .append(table.getName())
                .append(" WHERE ");

        builder.append(data.entrySet().stream()
                .map(entry -> entry.getKey() + "='" + MysqlUtils.escapeSql(String.valueOf(entry.getValue())) + "'")
                .collect(Collectors.joining(" AND ")));

        builder.append(';');

        return builder.toString();
    }
}
