package me.piggypiglet.docdex.documentation.index.data.storage.implementations;

import com.google.inject.Inject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.Indexes;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.db.objects.MongoDocumentedObject;
import me.piggypiglet.docdex.documentation.index.data.storage.IndexStorage;
import me.piggypiglet.docdex.documentation.index.data.utils.DataUtils;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MongoStorage implements IndexStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger("MongoStorage");
    private static final List<IndexModel> INDEXES = Stream.of("name")
            .map(Indexes::hashed)
            .map(IndexModel::new)
            .collect(Collectors.toList());

    private final MongoClient client;
    private final String database;

    @Inject
    public MongoStorage(@NotNull final MongoClient client, @NotNull final Config config) {
        this.client = client;
        this.database = config.getDatabase().getDatabase();
    }

    @Override
    public void save(@NotNull final Javadoc javadoc, @NotNull final Map<String, DocumentedObject> objects) {
        final String name = DataUtils.getName(javadoc);
        final MongoDatabase database = client.getDatabase(this.database);

        LOGGER.info("Attempting to save " + name + " to MongoDB.");

        try {
            database.createCollection(name);
        } catch (MongoCommandException exception) {
            LOGGER.info("Not saving " + name + " to MongoDB as it already exists.");
            return;
        }

        final MongoCollection<MongoDocumentedObject> collection = database.getCollection(name, MongoDocumentedObject.class);

        collection.createIndexes(INDEXES);
        collection.insertMany(objects.entrySet().stream()
                .map(entry -> new MongoDocumentedObject(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()));
        LOGGER.info("Saved " + name + " to mongo.");
    }

    @NotNull
    public Optional<DocumentedObject> get(@NotNull final Javadoc javadoc, @NotNull final String name) {
        final MongoCollection<MongoDocumentedObject> collection = client.getDatabase(database)
                .getCollection(DataUtils.getName(javadoc), MongoDocumentedObject.class);

        return Optional.ofNullable(collection.find(Filters.eq("name", name)).first())
                .map(MongoDocumentedObject::getObject);
    }
}
