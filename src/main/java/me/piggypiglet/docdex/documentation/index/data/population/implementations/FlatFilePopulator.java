package me.piggypiglet.docdex.documentation.index.data.population.implementations;

import com.google.gson.Gson;
import com.google.inject.util.Types;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.index.data.population.IndexPopulator;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.file.utils.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class FlatFilePopulator implements IndexPopulator {
    private static final Logger LOGGER = LoggerFactory.getLogger("FlatFilePopulator");
    private static final Gson GSON = new Gson();

    @Override
    public boolean shouldPopulate(final @NotNull Javadoc javadoc) {
        return new File("docs", String.join("-", javadoc.getNames()) + ".json").exists();
    }

    @SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent"})
    @NotNull
    @Override
    public Set<DocumentedObject> provideObjects(@NotNull final Javadoc javadoc) {
        final String fileName = String.join("-", javadoc.getNames()) + ".json";
        final File file = new File("docs", fileName);

        LOGGER.info("Loading pre-built index from " + fileName);

        try {
            return Optional.of((Set<DocumentedObject>) GSON.fromJson(FileUtils.readFile(file), Types.setOf(DocumentedObject.class)))
                    .stream()
                    // i don't like this but gotta get that log lol
                    .peek(set -> LOGGER.info("Finished loading " + fileName))
                    .findAny()
                    .get();
        } catch (IOException exception) {
            LOGGER.error("Something went wrong when loading " + fileName, exception);
        }

        return Collections.emptySet();
    }
}
