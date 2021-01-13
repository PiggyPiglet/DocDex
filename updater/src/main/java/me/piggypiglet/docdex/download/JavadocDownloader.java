package me.piggypiglet.docdex.download;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class JavadocDownloader<S> {
    protected static final Logger LOGGER = LoggerFactory.getLogger("Downloader");

    @Nullable
    protected abstract URL provideLatestUrl(@NotNull final S strategy);

    public void download(@NotNull final String path, @NotNull final S strategy)  {
        final URL url = provideLatestUrl(strategy);

        if (url == null) {
            return;
        }

        try (InputStream input = url.openStream();
             ReadableByteChannel readable = Channels.newChannel(input);
             FileChannel output = FileChannel.open(Paths.get(path), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                     StandardOpenOption.WRITE)
             ) {
            output.transferFrom(readable, 0, Long.MAX_VALUE);
        } catch (IOException exception) {
            LOGGER.error("Something went wrong when downloading " + url + " to " + path, exception);
        }
    }
}
