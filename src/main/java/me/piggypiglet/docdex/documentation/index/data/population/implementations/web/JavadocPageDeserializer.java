package me.piggypiglet.docdex.documentation.index.data.population.implementations.web;

import me.piggypiglet.docdex.documentation.index.data.population.implementations.web.components.TypeDeserializer;
import me.piggypiglet.docdex.documentation.index.data.population.implementations.web.components.method.MethodDeserializer;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.type.TypeMetadata;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
        final TypeMetadata typeMetadata = (TypeMetadata) type.getMetadata();
        final Set<DocumentedObject> methods = document.select(".methodDetails ul.blockList > li.blockList").stream()
                .map(element -> MethodDeserializer.deserialize(element, typeMetadata.getPackage(), type.getName()))
                .collect(Collectors.toSet());
        objects.addAll(methods);

        ((TypeMetadata) type.getMetadata()).getMethods().addAll(methods);

        return objects;
    }
}
