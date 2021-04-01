package me.piggypiglet.docdex.download.implementations;

import me.piggypiglet.docdex.config.strategies.direct.DirectZipStrategy;
import me.piggypiglet.docdex.download.framework.JavadocDownloader;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
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
}
