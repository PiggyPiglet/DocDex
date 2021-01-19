package me.piggypiglet.docdex.documentation.index.storage.implementations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.index.objects.DocumentedObjectKey;
import me.piggypiglet.docdex.documentation.index.population.implementations.flatfile.adaptation.ObjectMapAdapter;
import me.piggypiglet.docdex.documentation.index.storage.IndexStorage;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.utils.DataUtils;
import me.piggypiglet.docdex.file.utils.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static me.piggypiglet.docdex.documentation.index.population.implementations.flatfile.adaptation.ObjectMapAdapter.DESERIALIZED_TYPE;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class FlatFileStorage implements IndexStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger("FlatFileStorage");
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(DESERIALIZED_TYPE, new ObjectMapAdapter())
            .create();

    @Override
    public void save(@NotNull final Javadoc javadoc, @NotNull final Map<DocumentedObjectKey, DocumentedObject> objects) {
        final String fileName = DataUtils.getName(javadoc) + ".json";
        final Path file = Paths.get("docs", fileName);

        LOGGER.info("Attempting to save {}", fileName);

        if (Files.exists(file)) {
            LOGGER.info("{} already exists, not saving. Delete the file manually and restart the app if you wish to update the index.", fileName);
            return;
        } else {
            try {
                Files.createDirectories(file.getParent());
                Files.createFile(file);
            } catch (IOException exception) {
                LOGGER.error("Something went wrong when creating " + fileName, exception);
                return;
            }
        }

        try {
            FileUtils.writeFile(file, GSON.toJson(objects, DESERIALIZED_TYPE));
        } catch (IOException exception) {
            LOGGER.error("Something went wrong when saving " + fileName, exception);
            return;
        }

        LOGGER.info("Saved {}", fileName);
    }
}
