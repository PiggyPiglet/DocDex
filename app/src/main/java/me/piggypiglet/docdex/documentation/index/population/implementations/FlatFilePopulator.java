package me.piggypiglet.docdex.documentation.index.population.implementations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.util.Types;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.index.population.IndexPopulator;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.adaptation.creation.FieldMetadataCreator;
import me.piggypiglet.docdex.documentation.objects.adaptation.creation.MethodMetadataCreator;
import me.piggypiglet.docdex.documentation.objects.adaptation.creation.TypeMetadataCreator;
import me.piggypiglet.docdex.documentation.objects.detail.field.FieldMetadata;
import me.piggypiglet.docdex.documentation.objects.detail.method.MethodMetadata;
import me.piggypiglet.docdex.documentation.objects.type.TypeMetadata;
import me.piggypiglet.docdex.file.utils.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class FlatFilePopulator implements IndexPopulator {
    private static final Logger LOGGER = LoggerFactory.getLogger("FlatFilePopulator");
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(TypeMetadata.class, new TypeMetadataCreator())
            .registerTypeAdapter(MethodMetadata.class, new MethodMetadataCreator())
            .registerTypeAdapter(FieldMetadata.class, new FieldMetadataCreator())
            .create();
    private static final Type DESERIALIZED_TYPE = Types.mapOf(String.class, DocumentedObject.class);

    @Override
    public boolean shouldPopulate(final @NotNull Javadoc javadoc) {
        return new File("docs", String.join("-", javadoc.getNames()) + ".json").exists();
    }

    @SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent"})
    @NotNull
    @Override
    public Map<String, DocumentedObject> provideObjects(@NotNull final Javadoc javadoc) {
        final String fileName = String.join("-", javadoc.getNames()) + ".json";
        final File file = new File("docs", fileName);

        LOGGER.info("Loading pre-built index from " + fileName);

        try {
            return Optional.of((Map<String, DocumentedObject>) GSON.fromJson(FileUtils.readFile(file), DESERIALIZED_TYPE))
                    .stream()
                    // i don't like this but gotta get that log lol
                    .peek(set -> LOGGER.info("Finished loading " + fileName))
                    .findAny()
                    .get();
        } catch (IOException exception) {
            LOGGER.error("Something went wrong when loading " + fileName, exception);
        }

        return Collections.emptyMap();
    }
}
