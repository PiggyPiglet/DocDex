package me.piggypiglet.docdex.documentation.index.data.storage.implementations;

import com.google.inject.Inject;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.index.data.storage.IndexStorage;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MongoStorage implements IndexStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger("MongoStorage");

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
        }

        final MongoCollection<DocumentedObject> collection = database.getCollection(name, DocumentedObject.class);
        collection.insertMany(new ArrayList<>(objects));
    }
}
