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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class UpdateRegisterable extends Registerable {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
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
//        final CompletableFuture<?> future = CompletableFuture.allOf(config.getJavadocs().stream()
//                .map(javadoc -> CompletableFuture.runAsync(() -> {
//                    downloadManager.download(javadoc);
//                    updateManager.update(javadoc);
//                }, EXECUTOR))
//                .toArray(CompletableFuture[]::new));
//
//        future.thenAccept(empty -> {
//            updateManager.applyUpdates();
//
//            CompletableFuture.runAsync(new RestartTask(), EXECUTOR)
//                    .thenAccept(empty2 -> System.exit(0));
//        });

        final Set<UpdaterJavadoc> modifiedJavadocs = config.getJavadocs().parallelStream()
                .filter(downloadManager::download)
                .peek(javadoc -> {
                    LOGGER.info("Downloaded " + javadoc.getNames());
                    updateManager.update(javadoc);
                    LOGGER.info("Updating config entry for " + javadoc.getNames());
                })
                .collect(Collectors.toSet());

        updateManager.applyUpdates();
        LOGGER.info("Applied updates to config.json");
        pterodactylManager.deleteJavadocsAndStop(modifiedJavadocs);
        LOGGER.info("Deleting MongoDB collections for all updated javadocs.");
        // seems pterodactyl starts it up on it's own because it thinks it crashed
//        SCHEDULER.submit(new RestartTask());
    }

//    private final class RestartTask implements Runnable {
//        private final Logger logger = LoggerFactory.getLogger("Restart Task");
//
//        @Override
//        public void run() {
//            final GettablePowerState state = pterodactylManager.getPowerState();
//
//            if (state != GettablePowerState.OFFLINE) {
//                SCHEDULER.schedule(this, 1, TimeUnit.SECONDS);
//                return;
//            }
//
//            logger.info("Starting DocDex app.");
//
//            pterodactylManager.setPowerState(SettablePowerState.START);
//            System.exit(0);
//        }
//    }
}
