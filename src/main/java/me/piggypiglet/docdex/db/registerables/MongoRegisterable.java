package me.piggypiglet.docdex.db.registerables;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoDatabase;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.config.MongoConfig;
import me.piggypiglet.docdex.scanning.framework.Scanner;
import me.piggypiglet.docdex.scanning.rules.Rules;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MongoRegisterable extends Registerable {
    private final MongoConfig mongo;
    private final Scanner scanner;

    @Inject
    public MongoRegisterable(@NotNull final Config config, @NotNull final Scanner scanner) {
        this.mongo = config.getDatabase();
        this.scanner = scanner;
    }

    @Override
    protected void execute(@NotNull final Injector injector) {
        final Set<Codec<?>> codecs = scanner.getClasses(Rules.builder().typeExtends(Codec.class).disallowMutableClasses().build())
                .map(injector::getInstance)
                .map(codec -> (Codec<?>) codec)
                .collect(Collectors.toSet());
        final MongoClientOptions options = MongoClientOptions.builder()
                .codecRegistry(CodecRegistries.fromRegistries(
                        MongoClient.getDefaultCodecRegistry(),
                        CodecRegistries.fromCodecs(new ArrayList<>(codecs))
                ))
                .build();
        final MongoClient client = new MongoClient(mongo.getHost() + ':' + mongo.getPort(), options);

        addBinding(MongoClient.class, client);
        addBinding(MongoDatabase.class, client.getDatabase(mongo.getDatabase()));
    }
}
