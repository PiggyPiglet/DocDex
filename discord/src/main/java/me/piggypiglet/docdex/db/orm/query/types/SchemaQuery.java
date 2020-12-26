package me.piggypiglet.docdex.db.orm.query.types;

import me.piggypiglet.docdex.db.orm.query.Query;
import me.piggypiglet.docdex.db.orm.query.SqlDataTypes;
import me.piggypiglet.docdex.db.orm.structure.TableField;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class SchemaQuery implements Query {
    @Override
    @NotNull
    public Set<String> generate(@NotNull final TableStructure structure) {
        return Stream.concat(Stream.of(structure), Query.getAll(structure))
                .map(struct -> {
                    final StringBuilder builder = new StringBuilder();

                    builder.append("CREATE TABLE `")
                            .append(struct.getName())
                            .append("` (");

                    for (final TableField column : struct.getColumns()) {
                        builder.append("`")
                                .append(column.getName())
                                .append("` ")
                                .append(column.getType())
                                .append(" NOT NULL");

                        if (column.getType() == SqlDataTypes.TEXT) {
                            builder.append(" COLLATE 'utf8_general_ci'");
                        }

                        builder.append(", ");
                    }

                    final TableField identifier = struct.getIdentifier();

                    builder.append("PRIMARY KEY (`")
                            .append(identifier.getName())
                            .append('`');

                    if (identifier.getType() == SqlDataTypes.TEXT) {
                        builder.append("(")
                                .append(identifier.getType().getLength())
                                .append(")");
                    }

                    builder.append(")) COLLATE='utf8_general_ci' ENGINE=InnoDB;");

                    return builder.toString();
                }).collect(Collectors.toSet());
    }
}
