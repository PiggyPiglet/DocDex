package me.piggypiglet.docdex.documentation.index.data.population.registerables;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.index.DocumentationIndex;
import me.piggypiglet.docdex.documentation.index.data.population.IndexPopulator;
import me.piggypiglet.docdex.documentation.index.data.storage.IndexStorage;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class IndexPopulationRegisterable extends Registerable {
    private static final double BYTE_PER_MB = 1_000_000;
    private static final double MB_PER_DOC = 100;

    private static final Logger LOGGER = LoggerFactory.getLogger("Indexer");

    private final Set<Javadoc> javadocs;
    private final Set<IndexPopulator> populators;
    private final DocumentationIndex index;
    private final Set<IndexStorage> storageMechanisms;

    private final ExecutorService executor;

    @Inject
    public IndexPopulationRegisterable(@NotNull final Config config, @NotNull @Named("populators") final Set<IndexPopulator> populators,
                                       @NotNull final DocumentationIndex index, @NotNull @Named("storage") final Set<IndexStorage> storageMechanisms) {
        this.javadocs = config.getJavadocs();
        this.index = index;
        this.populators = populators;
        this.storageMechanisms = storageMechanisms;

        final double memory = (Runtime.getRuntime().totalMemory() / BYTE_PER_MB);
        final int threads = Math.min(javadocs.size(), (int) Math.ceil(memory / MB_PER_DOC));
        this.executor = Executors.newFixedThreadPool(threads);
        LOGGER.info("Spinning up thread pool with " + threads + " thread(s) for population.");
    }

    @Override
    protected void execute() {
        LOGGER.info("Attempting to index " + javadocs.size() + " javadoc(s).");

        javadocs.forEach(javadoc ->
                executor.execute(() ->
                        populators.stream().filter(populator -> populator.shouldPopulate(javadoc)).findAny().ifPresent(populator -> {
                            final Map<String, DocumentedObject> objects = populator.provideObjects(javadoc);
                            storageMechanisms.forEach(storage -> storage.save(javadoc, objects));
                            index.populate(javadoc, objects);
                        })
                )
        );

        executor.shutdown();
    }
}
