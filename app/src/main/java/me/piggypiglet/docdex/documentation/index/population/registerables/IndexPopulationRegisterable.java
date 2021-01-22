package me.piggypiglet.docdex.documentation.index.population.registerables;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.index.DocumentationIndex;
import me.piggypiglet.docdex.documentation.index.objects.DocumentedObjectKey;
import me.piggypiglet.docdex.documentation.index.population.IndexPopulator;
import me.piggypiglet.docdex.documentation.index.storage.IndexStorage;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.utils.DataUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class IndexPopulationRegisterable extends Registerable {
    private static final double BYTE_PER_MB = 1_000_000;
    private static final double MB_PER_DOC = 800;

    private static final Logger LOGGER = LoggerFactory.getLogger("Indexer");

    private final Set<Javadoc> javadocs;
    private final Set<IndexPopulator> populators;
    private final DocumentationIndex index;
    private final Set<IndexStorage> storageMechanisms;
    private final Set<CompletableFuture<?>> startupHooks;

    private final ExecutorService executor;

    @Inject
    public IndexPopulationRegisterable(@NotNull final Config config, @NotNull @Named("populators") final Set<IndexPopulator> populators,
                                       @NotNull final DocumentationIndex index, @NotNull @Named("storage") final Set<IndexStorage> storageMechanisms,
                                       @NotNull @Named("startup") final Set<CompletableFuture<?>> startupHooks) {
        this.javadocs = config.getJavadocs();
        this.index = index;
        this.populators = populators;
        this.storageMechanisms = storageMechanisms;
        this.startupHooks = startupHooks;

        final double memory = (Runtime.getRuntime().maxMemory() / BYTE_PER_MB);
        final int threads = Math.min(javadocs.size(), (int) Math.ceil(memory / MB_PER_DOC));
        this.executor = Executors.newFixedThreadPool(threads);
        LOGGER.info("Spinning up thread pool with {} thread(s) for population.", threads);
    }

    @Override
    public void execute() {
        LOGGER.info("Attempting to index {} javadoc(s).", javadocs.size());

        final Set<CompletableFuture<Void>> futures = new HashSet<>();
        javadocs.forEach(javadoc ->
                futures.add(CompletableFuture.runAsync(() ->
                        populators.stream().filter(populator -> populator.shouldPopulate(javadoc)).findAny().ifPresent(populator -> {
                            final Map<DocumentedObjectKey, DocumentedObject> objects = populator.provideObjects(javadoc);

                            if (objects.isEmpty()) {
                                LOGGER.error("No objects were indexed for {}", DataUtils.getName(javadoc) + '.');
                                return;
                            }

                            storageMechanisms.forEach(storage -> storage.save(javadoc, objects));
                            index.populate(javadoc, objects);
                            System.gc();
                        }), executor
                ).exceptionally(throwable -> {
                    LOGGER.error("", throwable);
                    return null;
                }))
        );
        startupHooks.add(CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[]{})));

        executor.shutdown();
    }
}
