package me.piggypiglet.docdex.documentation.index.data.population.implementations.web;

import com.google.common.collect.Lists;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.index.data.population.IndexPopulator;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.objects.type.TypeMetadata;
import me.piggypiglet.docdex.documentation.utils.DataUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
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
    public Map<String, DocumentedObject> provideObjects(@NotNull final Javadoc javadoc) {
        final long millis = System.currentTimeMillis();
        final String javadocName = DataUtils.getName(javadoc);
        final Document mainDocument = connect(javadoc.getLink());

        if (mainDocument == null) {
            return Collections.emptyMap();
        }

        final Optional<Element> indexAnchor = mainDocument.select("ul.navList > li > a").stream()
                .filter(element -> element.text().equalsIgnoreCase("index"))
                .findAny();

        if (indexAnchor.isEmpty()) {
            return Collections.emptyMap();
        }

        final List<Document> documents = Lists.newArrayList(connect(indexAnchor.get().absUrl("href")));
        final Document firstDocument = documents.get(0);

        if (firstDocument == null) {
            return Collections.emptyMap();
        }

        if (firstDocument.location().endsWith("index-1.html")) {
            firstDocument.selectFirst(".contentContainer > h2.title").previousElementSiblings().select("a").stream()
                    .filter(a -> a.hasAttr("href"))
                    .map(a -> a.absUrl("href"))
                    .filter(url -> !url.endsWith("index-1.html"))
                    .map(WebCrawlPopulator::connect)
                    .forEach(documents::add);
        }

        final Set<DocumentedObject> objects = new HashSet<>();
        final Set<Map.Entry<String, String>> types = documents.stream().flatMap(document -> document.select("dl > dt > a").stream())
                .filter(element -> TYPE_NAMES.stream().anyMatch(element.attr("title").toLowerCase()::startsWith))
                .map(element -> Map.entry(element.absUrl("href"), element.attr("href")))
                .collect(Collectors.toSet());

        LOGGER.info("Indexing " + types.size() + " types for " + javadocName);

        final AtomicInteger i = new AtomicInteger();
        final AtomicInteger previousPercentage = new AtomicInteger();
        types.parallelStream().forEach(completesExceptionally(entry -> {
            synchronized (i) {
                final int percentage = (int) ((100D / types.size()) * i.getAndIncrement());

                if (percentage % 10 == 0 && percentage != previousPercentage.get()) {
                    LOGGER.info(percentage + "% done on type indexing for " + javadocName);
                    previousPercentage.set(percentage);
                }
            }

            final Document page = connect(entry.getKey());

            if (page == null) {
                return;
            }

            objects.addAll(JavadocPageDeserializer.deserialize(page, javadoc.getActualLink() + '/' + entry.getValue().replace("../", "")));
        }));

        final Map<String, DocumentedObject> map = new HashMap<>();

        objects.forEach(object -> {
            map.put(DataUtils.getFqn(object).toLowerCase(), object);
            map.put(DataUtils.getName(object).toLowerCase(), object);
        });

        LOGGER.info("Indexing type children with parent methods for " + javadocName);

        i.set(0);
        previousPercentage.set(0);
        for (final DocumentedObject type : objects) {
            final int percentage = (int) ((100D / objects.size()) * i.getAndIncrement());

            if (percentage % 10 == 0 && percentage != previousPercentage.get()) {
                LOGGER.info(percentage + "% done on child method indexing for " + javadocName);
                previousPercentage.set(percentage);
            }

            if (!DocumentedTypes.isType(type.getType())) {
                continue;
            }

            getChildren(map, type).forEach(heir ->
                    ((TypeMetadata) type.getMetadata()).getMethods().stream()
                            .map(String::toLowerCase)
/*                            .peek(method -> {
                                if (map.get(method) == null) {
                                    System.out.println(type.getName() + " - " + heir + " - " + method);
                                }
                            })*/
                            .map(map::get)
                            .filter(Objects::nonNull)
                            .forEach(method -> {
                                final String addendum = '#' + method.getName().toLowerCase();

                                map.put(DataUtils.getFqn(heir).toLowerCase() + addendum, method);
                                map.put(DataUtils.getName(heir).toLowerCase() + addendum, method);
                            })
            );
        }

        LOGGER.info("Finished indexing " + javadocName + " in " + (System.currentTimeMillis() - millis) / 1000 + " second(s).");
        return map;
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

    @NotNull
    private static Set<DocumentedObject> getChildren(@NotNull final Map<String, DocumentedObject> map, @NotNull final DocumentedObject object) {
        final TypeMetadata typeMetadata = (TypeMetadata) object.getMetadata();

        final Set<DocumentedObject> subClasses = convertFromFqn(map, typeMetadata.getSubClasses());
        final Set<DocumentedObject> subInterfaces = convertFromFqn(map, typeMetadata.getSubInterfaces());
        final Set<DocumentedObject> implementingClasses = convertFromFqn(map, typeMetadata.getImplementingClasses());

        if (subClasses.isEmpty() && subInterfaces.isEmpty() && implementingClasses.isEmpty()) {
            return Collections.emptySet();
        }

        return Stream.of(
                subClasses,
                subInterfaces,
                implementingClasses
        )
                .flatMap(Set::stream)
                .flatMap(heir -> Stream.concat(Stream.of(heir), getChildren(map, heir).stream()))
                .collect(Collectors.toSet());
    }

    @NotNull
    private static Set<DocumentedObject> convertFromFqn(@NotNull final Map<String, DocumentedObject> map,
                                                        @NotNull final Set<String> fqns) {
        return fqns.stream()
                .map(String::toLowerCase)
                .map(map::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @NotNull
    private static <T> Consumer<T> completesExceptionally(@NotNull final Consumer<T> function) {
        return (t) -> {
            try {
                function.accept(t);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        };
    }
}
