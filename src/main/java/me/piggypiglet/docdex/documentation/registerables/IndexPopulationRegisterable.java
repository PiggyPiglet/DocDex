package me.piggypiglet.docdex.documentation.registerables;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.deserialization.JavadocPageDeserializer;
import me.piggypiglet.docdex.documentation.index.DocumentationIndex;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class IndexPopulationRegisterable extends Registerable {
    private static final Logger LOGGER = LoggerFactory.getLogger("Indexer");

    private final Set<Javadoc> javadocs;
    private final DocumentationIndex index;

    private final ExecutorService executor;

    @Inject
    public IndexPopulationRegisterable(@NotNull final Config config, @NotNull final DocumentationIndex index) {
        this.javadocs = config.getJavadocs();
        this.index = index;

        this.executor = Executors.newFixedThreadPool(javadocs.size());
    }

    @Override
    protected void execute() {
        LOGGER.info("Attempting to index " + javadocs.size() + " javadoc(s).");

        javadocs.forEach(javadoc -> executor.execute(() -> {
            final Document mainDocument = connect(javadoc.getLink());

            if (mainDocument == null) {
                return;
            }

            final Optional<Element> indexAnchor = mainDocument.select("ul.navList > li > a").stream()
                    .filter(element -> element.text().equalsIgnoreCase("index"))
                    .findAny();

            if (indexAnchor.isEmpty()) {
                return;
            }

            final Document document = connect(indexAnchor.get().absUrl("href"));

            if (document == null) {
                return;
            }

            final Set<DocumentedObject> objects = new HashSet<>();
            final Elements types = document.select("dl > dt > a > span.typeNameLink");

            LOGGER.info("Indexing " + types.size() + " types for " + javadoc.getLink());

            int i = 0;
            int previousPercentage = 0;
            for (final Element element : types) {
                final int percentage = (int) ((100D / types.size()) * i++);

                if (percentage % 10 == 0 && percentage != previousPercentage) {
                    LOGGER.info(percentage + "% done on " + javadoc.getLink());
                    previousPercentage = percentage;
                }

                final Document page = connect(element.parent().absUrl("href"));

                if (page == null) continue;

                objects.addAll(JavadocPageDeserializer.deserialize(page));
            }

            index.populate(javadoc, objects);
            LOGGER.info("Finished indexing " + javadoc.getLink());
        }));

        executor.shutdown();
    }

    @Nullable
    private static Document connect(@NotNull final String url) {
        try {
            return Jsoup.connect(url).maxBodySize(0).timeout(10000).get();
        } catch (IOException exception) {
            LOGGER.error("Something went wrong when connecting to " + url, exception);
        }

        return null;
    }
}
