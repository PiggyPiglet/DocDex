package me.piggypiglet.docdex.file;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.file.adaptation.FileAdapter;
import me.piggypiglet.docdex.file.exceptions.FileLoadException;
import me.piggypiglet.docdex.file.exceptions.FileSaveException;
import me.piggypiglet.docdex.file.utils.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class FileManager {
    private final FileAdapter adapter;
    private Gson gson;

    @Inject
    public FileManager(@NotNull final FileAdapter adapter, @NotNull @Named("files") final Map<Class<?>, Object> fileObjects) {
        this.adapter = adapter;
        this.gson = generateBuilder(fileObjects).create();
    }

    @NotNull
    public GsonBuilder generateBuilder(@NotNull final Map<Class<?>, Object> fileObjects) {
        final AtomicReference<GsonBuilder> builder = new AtomicReference<>(new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES));

        fileObjects.forEach((clazz, instance) -> builder.set(builder.get()
                .registerTypeAdapter(clazz, instanceCreator(instance))));

        return builder.get();
    }

    public void setGson(@NotNull final Gson gson) {
        this.gson = gson;
    }

    public boolean loadFile(@NotNull final Class<?> file, @NotNull final String internalPath,
                            @NotNull final String externalPath) {
        try {
            final boolean exists = exists(externalPath);
            gson.fromJson(gson.toJsonTree(adapter.fromString(FileUtils.readFile(createFile(internalPath, externalPath, file)))), file);
            return exists;
        } catch (IOException exception) {
            throw new FileLoadException(exception);
        }
    }

    public void saveFile(@NotNull final String externalPath, @NotNull final Object object) {
        try {
            writeFile(externalPath, adapter.toString(gson.fromJson(gson.toJsonTree(object), FileAdapter.MAP_TYPE)));
        } catch (IOException exception) {
            throw new FileSaveException(exception);
        }
    }

    @NotNull
    private Path createFile(@NotNull final String internalPath, @NotNull final String externalPath,
                            @NotNull final Class<?> clazz) throws IOException {
        return FileUtils.createFile(internalPath, externalPath, clazz);
    }

    private void writeFile(@NotNull final String path, @NotNull final String content) throws IOException {
        FileUtils.writeFile(Paths.get(path), content);
    }

    private boolean exists(@NotNull final String externalPath) {
        return new File(externalPath).exists();
    }

    @NotNull
    private static <T> InstanceCreator<T> instanceCreator(@NotNull final T instance) {
        return type -> instance;
    }
}
