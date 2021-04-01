package me.piggypiglet.docdex.download.framework;

import me.piggypiglet.docdex.config.strategies.UpdateStrategy;
import me.piggypiglet.docdex.download.utils.ZipUtils;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class JavadocDownloader<S extends UpdateStrategy> {
    private static final char SEPARATOR = File.separatorChar;

    protected static final Logger LOGGER = LoggerFactory.getLogger("Downloader");

    @NotNull
    protected abstract Set<URI> provideLatestUris(@NotNull final S strategy);

    public boolean download(@NotNull final S strategy)  {
        final Set<URI> uris = provideLatestUris(strategy);

        if (uris.isEmpty()) {
            return false;
        }

        String path = strategy.getPath();
        path = path.endsWith(SEPARATOR + "") ? path.substring(0, path.length() - 1) : path;
        final String zip = path + ".zip";

        try {
            FileUtils.deleteDirectory(path);
            Files.deleteIfExists(Paths.get(zip));
        } catch (IOException exception) {
            LOGGER.error("Something went wrong when deleting " + path + " and " + zip, exception);
            return false;
        }

        for (final URI uri : uris) {
            try (InputStream input = uri.toURL().openStream();
                 ReadableByteChannel readable = Channels.newChannel(input);
                 FileChannel output = FileChannel.open(Paths.get(zip), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                         StandardOpenOption.WRITE)
            ) {
                output.transferFrom(readable, 0, Long.MAX_VALUE);
                LOGGER.info("Successfully downloaded {} to {}", uri, zip);
                break;
            } catch (IOException exception) {
                LOGGER.info("Failed downloading {} to {}", uri, zip);
            }
        }

        try {
            ZipUtils.unzip(zip, path);
            LOGGER.info("Successfully unzipped {} to {}", zip, path);
        } catch (IOException exception) {
            LOGGER.error("Something went wrong when unzipping " + zip + " to " + path, exception);
            return false;
        }

        return true;
    }
}
