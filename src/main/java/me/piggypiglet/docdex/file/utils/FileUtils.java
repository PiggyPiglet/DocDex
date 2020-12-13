package me.piggypiglet.docdex.file.utils;

import com.google.common.io.Resources;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class FileUtils {
    private static final Pattern LINE_DELIMITER = Pattern.compile("\n");

    private FileUtils() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @NotNull
    public static File createFile(@NotNull final String internalPath, @NotNull final String externalPath,
                                  @NotNull final Class<?> clazz) throws IOException {
        final File file = new File(externalPath);

        if (file.exists()) return file;

        Optional.ofNullable(file.getParentFile()).ifPresent(File::mkdirs);
        file.createNewFile();

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
    public static String readEmbeddedFile(@NotNull final String path, @NotNull final Class<?> clazz) throws Exception {
        return Resources.toString(clazz.getResource(path), StandardCharsets.UTF_8);
    }

    @NotNull
    public static String readFile(@NotNull final File file) throws IOException {
        return String.join("\n", Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
    }

    public static void writeFile(@NotNull final File file, @NotNull final String content) throws IOException {
        Files.write(file.toPath(), Arrays.asList(LINE_DELIMITER.split(content)), StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING);
    }
}
