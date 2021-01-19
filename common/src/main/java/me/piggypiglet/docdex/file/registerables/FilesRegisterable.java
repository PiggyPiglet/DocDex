package me.piggypiglet.docdex.file.registerables;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.file.FileManager;
import me.piggypiglet.docdex.file.annotations.File;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class FilesRegisterable extends Registerable {
    private static final Logger LOGGER = LoggerFactory.getLogger("FileManager");

    private final Set<Class<?>> fileClasses;
    private final FileManager fileManager;

    @Inject
    public FilesRegisterable(@NotNull @Named("files") final Set<Class<?>> fileClasses, @NotNull final FileManager fileManager) {
        this.fileClasses = fileClasses;
        this.fileManager = fileManager;
    }

    @Override
    public void execute() {
        for (final Class<?> clazz : fileClasses) {
            final File data = clazz.getAnnotation(File.class);

            if (!fileManager.loadFile(clazz, data.internalPath(), data.externalPath()) && data.stopOnFirstCreate()) {
                LOGGER.info("This is the first time you've ran this program. Please populate the generated {} file before starting the app again.", data.externalPath());

                System.exit(0);
            }
        }
    }
}
