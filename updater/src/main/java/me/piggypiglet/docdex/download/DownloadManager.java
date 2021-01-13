package me.piggypiglet.docdex.download;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.config.UpdaterJavadoc;
import me.piggypiglet.docdex.config.strategies.UpdateStrategy;
import me.piggypiglet.docdex.download.framework.JavadocDownloader;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class DownloadManager {
    private final Map<Class<?>, JavadocDownloader<?>> downloaders;

    @Inject
    public DownloadManager(@NotNull @Named("downloaders") final Map<Class<?>, JavadocDownloader<?>> downloaders) {
        this.downloaders = downloaders;
    }

    public void download(@NotNull final UpdaterJavadoc javadoc) {
        final UpdateStrategy strategy = javadoc.getStrategy();

        if (strategy == null) {
            return;
        }

        final JavadocDownloader<?> downloader = downloaders.get(strategy.getClass());
        download(downloader, strategy, strategy.getPath());
    }

    @SuppressWarnings("unchecked")
    private <S extends UpdateStrategy> void download(@NotNull final JavadocDownloader<S> downloader, @NotNull final UpdateStrategy strategy,
                                                     @NotNull final String path) {
        downloader.download(path, (S) strategy);
    }
}
