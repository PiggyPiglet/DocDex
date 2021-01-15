package me.piggypiglet.docdex.download.framework;

import me.piggypiglet.docdex.config.strategies.UpdateStrategy;
import me.piggypiglet.docdex.download.utils.ZipUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
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
    protected abstract Set<URL> provideLatestUrl(@NotNull final S strategy);

    public boolean download(@NotNull final S strategy)  {
        final Set<URL> urls = provideLatestUrl(strategy);

        if (urls.isEmpty()) {
            return false;
        }

        String path = strategy.getPath();
        path = path.endsWith(SEPARATOR + "") ? path.substring(0, path.length() - 1) : path;
        final String jar = path + ".jar";

        new File(path).delete();
        new File(jar).delete();

        for (final URL url : urls) {
            try (InputStream input = url.openStream();
                 ReadableByteChannel readable = Channels.newChannel(input);
                 FileChannel output = FileChannel.open(Paths.get(jar), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                         StandardOpenOption.WRITE)
            ) {
                output.transferFrom(readable, 0, Long.MAX_VALUE);
                LOGGER.info("Successfully downloaded " + url + " to " + jar);
                break;
            } catch (IOException exception) {
                LOGGER.info("Failed downloading " + url + " to " + jar);
            }
        }

        try {
            ZipUtils.unzip(jar, path);
            LOGGER.info("Successfully unzipped " + jar + " to " + path);
        } catch (IOException exception) {
            LOGGER.error("Something went wrong when unzipping " + jar + " to " + path, exception);
            return false;
        }

        return true;
    }
}
