package me.piggypiglet.docdex.documentation.index.population.implementations.web;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.index.objects.DocumentedObjectKey;
import me.piggypiglet.docdex.documentation.index.population.IndexPopulator;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.objects.detail.method.MethodMetadata;
import me.piggypiglet.docdex.documentation.objects.type.TypeMetadata;
import me.piggypiglet.docdex.documentation.utils.DataUtils;
import me.piggypiglet.docdex.documentation.utils.ParameterTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
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
    private static final Map<ParameterTypes, String> DEFAULT_PARAMETERS = Arrays.stream(ParameterTypes.values())
            .collect(Collectors.toMap(parameterType -> parameterType, parameterType -> ""));

    @Override
    public boolean shouldPopulate(final @NotNull Javadoc javadoc) {
        return !(new File("docs", String.join("-", javadoc.getNames()) + ".json").exists());
    }

    @NotNull
    @Override
    public Map<DocumentedObjectKey, DocumentedObject> provideObjects(@NotNull final Javadoc javadoc) {
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
        final Set<Map.Entry<String, String>> types = documents.stream().flatMap(document -> document.select("dl > dt").stream())
                .map(element -> element.selectFirst("a"))
                .filter(element -> TYPE_NAMES.stream().anyMatch(element.attr("title").toLowerCase()::startsWith))
                .map(element -> Map.entry(element.absUrl("href"), element.attr("href")
                        .replace("../", "").replace("./", "")))
                .collect(Collectors.toSet());

        LOGGER.info("Indexing {} types for {}", types.size(), javadocName);

        final AtomicInteger i = new AtomicInteger();
        final AtomicInteger previousPercentage = new AtomicInteger();
        types.parallelStream().forEach(completesExceptionally(entry -> {
            synchronized (i) {
                final int percentage = (int) ((100D / types.size()) * i.getAndIncrement());

                if (percentage % 10 == 0 && percentage != previousPercentage.get()) {
                    LOGGER.info("{}% done on type indexing for {}", percentage, javadocName);
                    previousPercentage.set(percentage);
                }
            }

            final Document page = connect(entry.getKey());

            if (page == null) {
                return;
            }

            objects.addAll(JavadocPageDeserializer.deserialize(page, javadoc.getActualLink() + '/' + entry.getValue()));
        }));

        final Map<DocumentedObjectKey, DocumentedObject> map = objects.stream()
                .collect(Collectors.toMap(object -> new DocumentedObjectKey(
                        DataUtils.getName(object).toLowerCase(),
                        DataUtils.getFqn(object).toLowerCase(),
                        object.getMetadata() instanceof MethodMetadata ? DataUtils.getParams(object) : DEFAULT_PARAMETERS
                ), object -> object, (object1, object2) -> object1));
        final Multimap<String, DocumentedObject> fqns = HashMultimap.create();
        map.forEach((key, value) -> fqns.put(key.getFqn(), value));

        LOGGER.info("Indexing type children with parent methods for {}", javadocName);

        i.set(0);
        previousPercentage.set(0);
        for (final DocumentedObject type : objects) {
            final int percentage = (int) ((100D / objects.size()) * i.getAndIncrement());

            if (percentage % 10 == 0 && percentage != previousPercentage.get()) {
                LOGGER.info("{}% done on child method indexing for {}", percentage, javadocName);
                previousPercentage.set(percentage);
            }

            if (!DocumentedTypes.isType(type.getType())) {
                continue;
            }

            getChildren(fqns, type).forEach(heir ->
                    ((TypeMetadata) type.getMetadata()).getMethods().stream()
                            .map(String::toLowerCase)
                            .map(fqns::get)
                            .flatMap(Collection::stream)
                            .forEach(method -> {
                                final String addendum = '#' + method.getName().toLowerCase();

                                map.put(new DocumentedObjectKey(
                                        DataUtils.getName(heir).toLowerCase() + addendum,
                                        DataUtils.getFqn(heir).toLowerCase() + addendum,
                                        DataUtils.getParams(method)
                                ), method);
                            })
            );
        }

        LOGGER.info("Finished indexing {} in {} second(s).", javadocName, (System.currentTimeMillis() - millis) / 1000);
        return map;
    }

    @Nullable
    private static Document connect(@NotNull final String url) {
        try {
            return Jsoup.connect(url).maxBodySize(0).get();
        } catch (ConnectException exception) {
            LOGGER.error("Something went wrong when connecting to {}, is the link valid, and are the javadocs actually there?", url);
        } catch (IOException exception) {
            LOGGER.error("Something went wrong when connecting to " + url, exception);
        }

        return null;
    }

    @NotNull
    private static Set<DocumentedObject> getChildren(@NotNull final Multimap<String, DocumentedObject> map, @NotNull final DocumentedObject object) {
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
    private static Set<DocumentedObject> convertFromFqn(@NotNull final Multimap<String, DocumentedObject> map,
                                                        @NotNull final Set<String> fqns) {
        return fqns.stream()
                .map(String::toLowerCase)
                .map(map::get)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @NotNull
    private static <T> Consumer<T> completesExceptionally(@NotNull final Consumer<T> function) {
        return t -> {
            try {
                function.accept(t);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        };
    }
}
