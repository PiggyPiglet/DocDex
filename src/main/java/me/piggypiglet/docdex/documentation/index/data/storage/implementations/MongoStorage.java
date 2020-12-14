package me.piggypiglet.docdex.documentation.index.data.storage.implementations;

import com.google.inject.Inject;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.Indexes;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.db.objects.MongoDocumentedObject;
import me.piggypiglet.docdex.documentation.index.data.storage.IndexStorage;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MongoStorage implements IndexStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger("MongoStorage");
    private static final List<IndexModel> INDEXES = Stream.of("name", "fqn")
            .map(Indexes::hashed)
            .map(IndexModel::new)
            .collect(Collectors.toList());

    private final MongoDatabase database;

    @Inject
    public MongoStorage(@NotNull final MongoDatabase database) {
        this.database = database;
    }

    @Override
    public void save(@NotNull final Javadoc javadoc, @NotNull final Set<DocumentedObject> objects) {
        final String name = String.join("-", javadoc.getNames());

        LOGGER.info("Attempting to save " + name + " to mongo.");

        try {
            database.createCollection(name);
        } catch (MongoCommandException exception) {
            LOGGER.info("Not saving " + name + " to mongo as it already exists.");
            return;
        }

        final MongoCollection<MongoDocumentedObject> collection = database.getCollection(name, MongoDocumentedObject.class);

        collection.createIndexes(INDEXES);
        collection.insertMany(objects.stream()
                .map(MongoDocumentedObject::from)
                .collect(Collectors.toList()));
        LOGGER.info("Saved " + name + " to mongo.");
    }
}
