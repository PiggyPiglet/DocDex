package me.piggypiglet.docdex.db.registerables;

import co.aikar.idb.Database;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.PooledDatabaseOptions;
import com.google.inject.Inject;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.config.MysqlConfig;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MysqlRegisterable extends Registerable {
    private final MysqlConfig config;

    @Inject
    public MysqlRegisterable(@NotNull final Config config) {
        this.config = config.getMysql();
    }

    @Override
    public void execute() {
        final DatabaseOptions options = DatabaseOptions.builder()
                .mysql(config.getUsername(), config.getPassword(), config.getDatabase(), config.getHost() + ':' + config.getPort())
                .dataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource")
                .build();
        final Database database = PooledDatabaseOptions.builder()
                .options(options)
                .maxConnections(config.getPoolSize())
                .createHikariDatabase();
        addBinding(Database.class, database);
    }
}
