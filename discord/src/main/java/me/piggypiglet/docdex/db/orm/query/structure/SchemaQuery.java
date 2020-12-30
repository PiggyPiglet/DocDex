package me.piggypiglet.docdex.db.orm.query.structure;

import me.piggypiglet.docdex.db.orm.structure.TableColumn;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class SchemaQuery implements StructureQuery {
    @NotNull
    @Override
    @Language("SQL")
    public String generate(final @NotNull TableStructure structure) {
        final StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE `")
                .append(structure.getName())
                .append("` (");

        structure.getColumns().forEach(column ->
                builder.append("`")
                        .append(column.getName())
                        .append("` ")
                        .append(column.getDataStructure())
                        .append(" NOT NULL, "));

        final Set<TableColumn> identifiers = structure.getIdentifiers();

        builder.append("PRIMARY KEY (");

        builder.append(identifiers.stream()
                .map(identifier -> {
                    final String key = '`' + identifier.getName() + '`';
                    final int length = identifier.getLength();

                    if (length != -1) {
                        return key + '(' + length + ')';
                    }

                    return key;
                }).collect(Collectors.joining(", ")));

        builder.append(")) COLLATE='utf8_general_ci' ENGINE=InnoDB;");

        return builder.toString();
    }
}
