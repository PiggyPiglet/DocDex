package me.piggypiglet.docdex.documentation.index.storage.implementations;

import com.google.inject.Inject;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.Indexes;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.db.objects.MongoDocumentedObject;
import me.piggypiglet.docdex.documentation.index.storage.IndexStorage;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.utils.DataUtils;
import me.piggypiglet.docdex.documentation.utils.ParameterTypes;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MongoStorage implements IndexStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger("MongoStorage");
    private static final List<IndexModel> INDEXES = Stream.of("identifier")
            .map(Indexes::hashed)
            .map(IndexModel::new)
            .collect(Collectors.toList());

    private final MongoDatabase database;

    @Inject
    public MongoStorage(@NotNull final MongoDatabase database) {
        this.database = database;
    }

    @Override
    public void save(@NotNull final Javadoc javadoc, @NotNull final Map<String, DocumentedObject> objects) {
        final String javadocName = DataUtils.getName(javadoc);

        LOGGER.info("Attempting to save " + javadocName + " to MongoDB.");

        try {
            database.createCollection(javadocName);
        } catch (MongoCommandException exception) {
            LOGGER.info("Not saving " + javadocName + " to MongoDB as it already exists.");
            return;
        }

        final MongoCollection<MongoDocumentedObject> collection = database.getCollection(javadocName, MongoDocumentedObject.class);
        final Map<DocumentedObject, Set<MongoDocumentedObject.Builder>> mongoObjects = new HashMap<>();

        objects.forEach((key, object) -> {
            mongoObjects.putIfAbsent(object, new HashSet<>());

            final boolean fqn = key.contains(".");
            final Set<MongoDocumentedObject.Builder> builders = mongoObjects.get(object).stream()
                    .filter(builder -> fqn ? builder.getFqn() == null : builder.getName() == null)
                    .collect(Collectors.toSet());

            if (builders.isEmpty()) {
                final MongoDocumentedObject.Builder builder = MongoDocumentedObject.builder(object);
                mongoObjects.get(object).add(builder);

                if (fqn) {
                    builder.fqn(key);
                } else {
                    builder.name(key);
                }
            } else {
                builders.forEach(builder -> {
                    if (fqn) {
                        builder.fqn(key);
                    } else {
                        builder.name(key);
                    }
                });
            }
        });

        mongoObjects.forEach((object, builders) -> builders.forEach(builder -> {
            final String fqn = builder.getFqn();

            if (object.getType() == DocumentedTypes.METHOD || object.getType() == DocumentedTypes.CONSTRUCTOR) {
                final Map<ParameterTypes, String> params = DataUtils.getParams(object);
                final String name = builder.getName();

                final String fullParams = '(' + params.get(ParameterTypes.FULL) + ')';
                final String typeParams = '(' + params.get(ParameterTypes.TYPE) + ')';
                final String nameParams = '(' + params.get(ParameterTypes.NAME) + ')';

                builder.identifier(fqn + fullParams)
                        .fullParams(name + fullParams)
                        .typeParams(name + typeParams)
                        .fqnTypeParams(fqn + typeParams)
                        .nameParams(name + nameParams)
                        .fqnNameParams(fqn + nameParams);
            } else {
                builder.identifier(fqn)
                        .fullParams("")
                        .typeParams("")
                        .fqnTypeParams("")
                        .nameParams("")
                        .fqnNameParams("");
            }
        }));

        collection.createIndexes(INDEXES);
        collection.insertMany(mongoObjects.values().stream()
                .flatMap(Collection::stream)
                .map(MongoDocumentedObject.Builder::build)
                .collect(Collectors.toList()));
        LOGGER.info("Saved " + javadocName + " to mongo.");
    }

    @NotNull
    public Optional<DocumentedObject> get(@NotNull final Javadoc javadoc, @NotNull final Map<String, String> filters) {
        final MongoCollection<MongoDocumentedObject> collection = database.getCollection(DataUtils.getName(javadoc), MongoDocumentedObject.class);
        final Bson filter = Filters.and(filters.entrySet().stream()
                .map(entry -> Filters.eq(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet()));

        return Optional.ofNullable(collection.find(filter).first())
                .map(MongoDocumentedObject::getObject);
    }
}
