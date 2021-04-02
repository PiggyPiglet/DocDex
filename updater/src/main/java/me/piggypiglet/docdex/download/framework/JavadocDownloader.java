package me.piggypiglet.docdex.download.framework;

import me.piggypiglet.docdex.config.strategies.UpdateStrategy;
import me.piggypiglet.docdex.download.utils.ZipUtils;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    protected static final Logger LOGGER = LoggerFactory.getLogger("Downloader");

    @NotNull
    protected abstract Set<URI> provideLatestUris(@NotNull final S strategy);

    protected boolean download(@NotNull final Set<URI> uris, @NotNull final String zip)  {
        if (uris.isEmpty()) {
            return false;
        }

        try {
            Files.deleteIfExists(Paths.get(zip));
        } catch (IOException exception) {
            LOGGER.error("Something went wrong when deleting " + zip, exception);
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

        return true;
    }

    protected boolean unzip(@NotNull final String zip, @NotNull final String path) {
        try {
            FileUtils.deleteDirectory(path);
            ZipUtils.unzip(zip, path);
            LOGGER.info("Successfully unzipped {} to {}", zip, path);
        } catch (IOException exception) {
            LOGGER.error("Something went wrong when unzipping " + zip + " to " + path, exception);
            return false;
        }

        return true;
    }

    public boolean execute(@NotNull final S strategy) {
        final String zip = strategy.getZip();
        final String path = strategy.getPath();

        if (download(provideLatestUris(strategy), zip)) {
            return unzip(zip, path);
        }

        return false;
    }
}
