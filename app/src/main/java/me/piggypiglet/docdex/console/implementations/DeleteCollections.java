package me.piggypiglet.docdex.console.implementations;

import com.google.inject.Inject;
import com.mongodb.client.MongoDatabase;
import me.piggypiglet.docdex.console.ConsoleCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DeleteCollections extends ConsoleCommand {
    private final MongoDatabase database;

    @Inject
    public DeleteCollections(@NotNull final MongoDatabase database) {
        super("delete", "delete collections from mongo then stop the app.");
        this.database = database;
    }

    @Override
    protected void execute(final @NotNull List<String> args) {
        args.forEach(collection -> database.getCollection(collection).drop());
        System.exit(0);
    }
}
