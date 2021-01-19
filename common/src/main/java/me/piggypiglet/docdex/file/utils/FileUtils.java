package me.piggypiglet.docdex.file.utils;

import com.google.common.io.Resources;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class FileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger("FileUtils");
    private static final Pattern LINE_DELIMITER = Pattern.compile("\n");

    private FileUtils() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    public static Path createFile(@NotNull final String internalPath, @NotNull final String externalPath,
                                  @NotNull final Class<?> clazz) throws IOException {
        final Path file = Paths.get(externalPath);

        if (Files.exists(file)) return file;

        final Optional<Path> parent = Optional.ofNullable(file.getParent());

        try {
            if (parent.isPresent()) {
                Files.createDirectories(parent.get());
            }

            Files.createFile(file);
        } catch (IOException exception) {
            LOGGER.error("Something went wrong when creating " + file, exception);
            System.exit(-1);
        }

        exportResource(internalPath, externalPath, clazz);
        return file;
    }

    public static void exportResource(@NotNull final String internalPath, @NotNull final String externalPath,
                                      @NotNull final Class<?> clazz) throws IOException {
        Files.copy(clazz.getResourceAsStream(internalPath), Paths.get(externalPath),
                StandardCopyOption.REPLACE_EXISTING);
    }

    @SuppressWarnings("UnstableApiUsage")
    @NotNull
    public static String readEmbeddedFile(@NotNull final String path, @NotNull final Class<?> clazz) throws IOException {
        return Resources.toString(clazz.getResource(path), StandardCharsets.UTF_8);
    }

    @NotNull
    public static String readFile(@NotNull final Path file) throws IOException {
        return String.join("\n", Files.readAllLines(file, StandardCharsets.UTF_8));
    }

    public static void writeFile(@NotNull final Path file, @NotNull final String content) throws IOException {
        Files.write(file, Arrays.asList(LINE_DELIMITER.split(content)), StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING);
    }
}
