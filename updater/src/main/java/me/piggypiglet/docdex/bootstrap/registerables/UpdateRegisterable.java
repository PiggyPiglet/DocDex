package me.piggypiglet.docdex.bootstrap.registerables;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.config.UpdaterJavadoc;
import me.piggypiglet.docdex.download.DownloadManager;
import me.piggypiglet.docdex.pterodactyl.PterodactylManager;
import me.piggypiglet.docdex.update.UpdateManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class UpdateRegisterable extends Registerable {
    private static final Logger LOGGER = LoggerFactory.getLogger("Update Registerable");

    private final Config config;
    private final DownloadManager downloadManager;
    private final UpdateManager updateManager;
    private final PterodactylManager pterodactylManager;

    @Inject
    public UpdateRegisterable(@NotNull final Config config, @NotNull final DownloadManager downloadManager,
                              @NotNull final UpdateManager updateManager, @NotNull final PterodactylManager pterodactylManager) {
        this.config = config;
        this.downloadManager = downloadManager;
        this.updateManager = updateManager;
        this.pterodactylManager = pterodactylManager;
    }

    @Override
    public void execute() {
        final Set<UpdaterJavadoc> modifiedJavadocs = config.getJavadocs().parallelStream()
                .filter(downloadManager::download)
                .collect(Collectors.toSet());

        modifiedJavadocs.forEach(javadoc -> {
            LOGGER.info("Downloaded {}", javadoc.getNames());
            updateManager.update(javadoc);
            LOGGER.info("Updating config entry for {}", javadoc.getNames());
        });
        updateManager.applyUpdates();
        LOGGER.info("Applied updates to config.json");
        pterodactylManager.deleteJavadocsAndStop(modifiedJavadocs);
        LOGGER.info("Deleting MongoDB collections for all updated javadocs.");
        System.exit(0);
    }
}
