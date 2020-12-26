package me.piggypiglet.docdex.db.orm.query.types;

import com.google.inject.Inject;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.db.orm.query.Query;
import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ExistsQuery implements Query {
    private final Config config;

    @Inject
    public ExistsQuery(@NotNull final Config config) {
        this.config = config;
    }

    @Override
    @NotNull
    public Set<String> generate(@NotNull final TableStructure table) {
        return Stream.concat(Stream.of(table), Query.getAll(table))
                .map(TableStructure::getName)
                .map(name -> "SHOW TABLES LIKE '" + name + "'")
                .collect(Collectors.toSet());
    }
}
