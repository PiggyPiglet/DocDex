package me.piggypiglet.docdex.file;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.file.exceptions.FileLoadException;
import me.piggypiglet.docdex.file.serialization.FileAdapter;
import me.piggypiglet.docdex.file.utils.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class FileManager {
    private static final String DATA_DIRECTORY = ".";

    private final FileAdapter adapter;
    private final Gson gson;

    @Inject
    public FileManager(@NotNull final FileAdapter adapter, @NotNull @Named("files") final Map<Class<?>, Object> fileObjects) {
        this.adapter = adapter;

        final AtomicReference<GsonBuilder> builder = new AtomicReference<>(new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES));

        fileObjects.forEach((clazz, instance) -> builder.set(builder.get()
                .registerTypeAdapter(clazz, instanceCreator(instance))));

        gson = builder.get().create();
    }

    @NotNull
    private static <T> InstanceCreator<T> instanceCreator(@NotNull final T instance) {
        return type -> instance;
    }

    public boolean loadFile(@NotNull final Class<?> file, @NotNull final String internalPath,
                         @NotNull final String externalPath) {
        try {
            final boolean exists = exists(externalPath);
            gson.fromJson(gson.toJsonTree(adapter.fromString(FileUtils.readFile(createFile(internalPath, externalPath, file)))), file);
            return exists;
        } catch (final Exception exception) {
            throw new FileLoadException(exception);
        }
    }

    @NotNull
    private File createFile(@NotNull final String internalPath, @NotNull final String externalPath,
                            @NotNull final Class<?> clazz) throws IOException {
        return FileUtils.createFile(internalPath, DATA_DIRECTORY + '/' + externalPath, clazz);
    }

    private boolean exists(@NotNull final String externalPath) {
        return new File(DATA_DIRECTORY + '/' + externalPath).exists();
    }
}
