package me.piggypiglet.docdex.download.utils;

import me.piggypiglet.docdex.download.utils.exceptions.ZipSlipException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ZipUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger("ZipUtils");

    private ZipUtils() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    public static void unzip(@NotNull final String archivePath, @NotNull final String destinationPath) throws IOException {
        final File destinationDirectory = new File(destinationPath);
        final byte[] buffer = new byte[1024];

        try (FileInputStream fileInput = new FileInputStream(archivePath);
             ZipInputStream zipInput = new ZipInputStream(fileInput)) {
            ZipEntry entry;

            while ((entry = zipInput.getNextEntry()) != null) {
                final File file = newFile(destinationDirectory, entry);

                if (entry.isDirectory()) {
                    if (!file.isDirectory() && !file.mkdirs()) {
                        throw new IOException("Failed to create directory " + file);
                    }
                } else {
                    final File parent = file.getParentFile();

                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + file);
                    }

                    try (FileOutputStream output = new FileOutputStream(file)) {
                        int length;

                        while ((length = zipInput.read(buffer)) > 0) {
                            output.write(buffer, 0, length);
                        }
                    }
                }

                zipInput.closeEntry();
            }
        }
    }

    @NotNull
    private static File newFile(@NotNull final File destinationDirectory, @NotNull final ZipEntry entry) throws IOException {
        final File destinationFile = new File(destinationDirectory, entry.getName());

        final String destinationDirectoryPath = destinationDirectory.getCanonicalPath();
        final String destinationFilePath = destinationFile.getCanonicalPath();

        if (!destinationFilePath.startsWith(destinationDirectoryPath + File.separatorChar)) {
            throw new ZipSlipException("Entry is outside of target directory: " + entry.getName() + ", possible zip slip attack.");
        }

        return destinationFile;
    }
}
