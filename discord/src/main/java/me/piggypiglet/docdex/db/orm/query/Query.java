package me.piggypiglet.docdex.db.orm.query;

import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Stream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public interface Query {
    @NotNull
    Set<String> generate(@NotNull final TableStructure table);

    @NotNull
    static Stream<TableStructure> getAll(@NotNull final TableStructure structure) {
        final Set<TableStructure> subs = structure.getSubStructures();

        return Stream.concat(subs.stream(), subs.stream()
                .flatMap(Query::getAll));
    }
}
