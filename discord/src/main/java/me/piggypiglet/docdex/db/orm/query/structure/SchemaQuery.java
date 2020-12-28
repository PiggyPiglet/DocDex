package me.piggypiglet.docdex.db.orm.query.structure;

import me.piggypiglet.docdex.db.orm.structure.TableColumn;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import me.piggypiglet.docdex.db.orm.structure.objects.SqlDataStructures;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

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

        final TableColumn identifier = structure.getIdentifier();

        builder.append("PRIMARY KEY (`")
                .append(identifier.getName())
                .append("`");

        final SqlDataStructures dataStructure = identifier.getDataStructure();

        if (dataStructure.getLength() != -1) {
            builder.append("(")
                    .append(dataStructure.getLength())
                    .append(")");
        }

        builder.append(")) COLLATE='utf8_general_ci' ENGINE=InnoDB;");

        return builder.toString();
    }
}
