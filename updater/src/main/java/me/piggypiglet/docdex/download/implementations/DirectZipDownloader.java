package me.piggypiglet.docdex.download.implementations;

import me.piggypiglet.docdex.config.strategies.direct.DirectZipStrategy;
import me.piggypiglet.docdex.download.framework.JavadocDownloader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DirectZipDownloader extends JavadocDownloader<DirectZipStrategy> {
    @NotNull
    @Override
    protected Set<URI> provideLatestUris(@NotNull final DirectZipStrategy strategy) {
        final String link = strategy.getLink();

        try {
            return Collections.singleton(new URI(link));
        } catch (URISyntaxException exception) {
            LOGGER.error("Something is wrong with the uri: " + link);
        }

        return Collections.emptySet();
    }

    @Override
    public boolean execute(final @NotNull DirectZipStrategy strategy) {
        final String zip = strategy.getZip();
        final String path = strategy.getPath();

        if (download(provideLatestUris(strategy), zip)) {
            return unzip(zip, path, strategy.getInnerPath());
        }

        return false;
    }

    private boolean unzip(@NotNull final String zip, @NotNull final String path,
                          @Nullable final String innerPath) {
        final boolean initialUnzip = unzip(zip, path);

        if (innerPath == null) {
            return initialUnzip;
        }

        try {
            final Path serializedInnerPath = Paths.get(path + '/' + innerPath);

            Files.list(serializedInnerPath).forEach(file -> {
                try {
                    Files.move(file, Paths.get(path, file.getFileName().toString()));
                } catch (IOException exception) {
                    LOGGER.error("Something went wrong when moving " + file + " to " + path + ".");
                }
            });
            Files.delete(serializedInnerPath);
        } catch (IOException exception) {
            LOGGER.error("Something went wrong when listing from " + innerPath + ".", exception);
            return false;
        }

        return true;
    }
}
