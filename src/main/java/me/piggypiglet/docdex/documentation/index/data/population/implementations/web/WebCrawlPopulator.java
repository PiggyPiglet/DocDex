package me.piggypiglet.docdex.documentation.index.data.population.implementations.web;

import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.index.data.population.IndexPopulator;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class WebCrawlPopulator implements IndexPopulator {
    private static final Logger LOGGER = LoggerFactory.getLogger("WebCrawlPopulator");
    private static final Set<String> TYPE_NAMES = Stream.of(
            DocumentedTypes.CLASS, DocumentedTypes.INTERFACE,
            DocumentedTypes.ANNOTATION, DocumentedTypes.ENUM
    ).map(DocumentedTypes::getName).map(String::toLowerCase).collect(Collectors.toSet());

    @Override
    public boolean shouldPopulate(final @NotNull Javadoc javadoc) {
        return !(new File("docs", String.join("-", javadoc.getNames()) + ".json").exists());
    }

    @NotNull
    @Override
    public Set<DocumentedObject> provideObjects(@NotNull final Javadoc javadoc) {
        final Document mainDocument = connect(javadoc.getLink());

        if (mainDocument == null) {
            return Collections.emptySet();
        }

        final Optional<Element> indexAnchor = mainDocument.select("ul.navList > li > a").stream()
                .filter(element -> element.text().equalsIgnoreCase("index"))
                .findAny();

        if (indexAnchor.isEmpty()) {
            return Collections.emptySet();
        }

        final Document document = connect(indexAnchor.get().absUrl("href"));

        if (document == null) {
            return Collections.emptySet();
        }

        final Set<DocumentedObject> objects = new HashSet<>();
        final Set<Element> types = document.select("dl > dt > a").stream()
                .filter(element -> TYPE_NAMES.stream().anyMatch(element.attr("title").toLowerCase()::startsWith))
                .collect(Collectors.toSet());

        LOGGER.info("Indexing " + types.size() + " types for " + javadoc.getLink());

        int i = 0;
        int previousPercentage = 0;
        for (final Element element : types) {
            final int percentage = (int) ((100D / types.size()) * i++);

            if (percentage % 10 == 0 && percentage != previousPercentage) {
                LOGGER.info(percentage + "% done on " + javadoc.getLink());
                previousPercentage = percentage;
            }

            final Document page = connect(element.absUrl("href"));

            if (page == null) continue;

            objects.addAll(JavadocPageDeserializer.deserialize(page));
        }

        LOGGER.info("Finished indexing " + javadoc.getLink());
        return objects;
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
