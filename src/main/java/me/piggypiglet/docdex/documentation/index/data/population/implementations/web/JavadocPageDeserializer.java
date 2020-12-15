package me.piggypiglet.docdex.documentation.index.data.population.implementations.web;

import me.piggypiglet.docdex.documentation.index.data.population.implementations.web.components.TypeDeserializer;
import me.piggypiglet.docdex.documentation.index.data.population.implementations.web.components.method.MethodDeserializer;
import me.piggypiglet.docdex.documentation.index.data.utils.DataUtils;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.type.TypeMetadata;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JavadocPageDeserializer {
    private JavadocPageDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    public static Set<DocumentedObject> deserialize(@NotNull final Document document) {
        final Set<DocumentedObject> objects = new HashSet<>();

        final DocumentedObject type = TypeDeserializer.deserialize(
                document.selectFirst(".contentContainer > .description"),
                document.selectFirst(".header > .subTitle")
        );
        objects.add(type);

        // we pass the owner as a string here to ensure there's no cyclic dependencies during the population process.
        final Stream<Element> elements = Optional.ofNullable(document.selectFirst(".methodDetails ul.blockList > li.blockList"))
                .map(element -> document.select(".methodDetails ul.blockList > li.blockList").stream())
                .orElseGet(() -> {
                    final Optional<Element> methods = document.select(".details > ul.blockList > li.blockList > ul.blockList > li.blockList > h3").stream()
                            .filter(element -> element.text().equalsIgnoreCase("method detail"))
                            .findAny();

                    if (methods.isEmpty()) {
                        return Stream.empty();
                    }

                    return methods.get().parent().select("ul > li.blockList").stream();
                });
        final Set<DocumentedObject> methods = elements
                .map(element -> MethodDeserializer.deserialize(element, type.getPackage(), type.getName()))
                .collect(Collectors.toSet());
        objects.addAll(methods);

        ((TypeMetadata) type.getMetadata()).getMethods().addAll(methods.stream()
                .map(DataUtils::getFqn)
                .collect(Collectors.toSet()));

        return objects;
    }
}
