package me.piggypiglet.docdex.documentation.index.storage.implementations;

import com.google.inject.Inject;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.db.objects.MongoDocumentedObject;
import me.piggypiglet.docdex.documentation.index.objects.DocumentedObjectKey;
import me.piggypiglet.docdex.documentation.index.storage.IndexStorage;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.utils.DataUtils;
import me.piggypiglet.docdex.documentation.utils.ParameterTypes;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MongoStorage implements IndexStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger("MongoStorage");
    private static final Bson INDEX = Indexes.hashed("identifier");

    private final MongoDatabase database;

    @Inject
    public MongoStorage(@NotNull final MongoDatabase database) {
        this.database = database;
    }

    @Override
    public void save(@NotNull final Javadoc javadoc, @NotNull final Map<DocumentedObjectKey, DocumentedObject> objects) {
        final String javadocName = DataUtils.getName(javadoc);

        LOGGER.info("Attempting to save " + javadocName + " to MongoDB.");

        try {
            database.createCollection(javadocName);
        } catch (MongoCommandException exception) {
            LOGGER.info("Not saving " + javadocName + " to MongoDB as it already exists.");
            return;
        }

        final MongoCollection<MongoDocumentedObject> collection = database.getCollection(javadocName, MongoDocumentedObject.class);
        final List<MongoDocumentedObject> mongoObjects = objects.entrySet().stream().map(entry -> {
            final DocumentedObjectKey key = entry.getKey();
            final DocumentedObject object = entry.getValue();

            final MongoDocumentedObject.Builder builder = MongoDocumentedObject.builder(object)
                    .name(key.getName())
                    .fqn(key.getFqn());
            final String fqn = key.getFqn();

            if (object.getType() == DocumentedTypes.METHOD || object.getType() == DocumentedTypes.CONSTRUCTOR) {
                final Map<ParameterTypes, String> params = DataUtils.getParams(object);
                final String name = key.getName();

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

            return builder.build();
        }).collect(Collectors.toList());

        collection.createIndex(INDEX);
        collection.insertMany(mongoObjects);
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
