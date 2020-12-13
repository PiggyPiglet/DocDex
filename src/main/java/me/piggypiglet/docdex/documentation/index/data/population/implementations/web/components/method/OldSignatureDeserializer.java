package me.piggypiglet.docdex.documentation.index.data.population.implementations.web.components.method;

import me.piggypiglet.docdex.documentation.index.data.population.implementations.web.utils.DeserializationUtils;
import me.piggypiglet.docdex.documentation.objects.method.DocumentedMethodBuilder;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class OldSignatureDeserializer {
    private static final Pattern ANNOTATION_PATTERN = Pattern.compile("@.+?(?=[ \\n])");
    private static final Pattern SPACE_DELIMITER = Pattern.compile(" ");


    private OldSignatureDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    static void deserialize(@NotNull final Element details, @NotNull final DocumentedMethodBuilder builder) {
        final Element pre = details.selectFirst("pre");
        final Matcher annotationMatcher = ANNOTATION_PATTERN.matcher(pre.text());

        final Set<String> annotations = new HashSet<>();
        while (annotationMatcher.find()) {
            annotations.add(annotationMatcher.group());
        }

        pre.select("a").stream()
                .filter(anchor -> annotations.contains(anchor.text()))
                .map(annotation -> annotation.text(annotation.text().substring(1)))
                .map(DeserializationUtils::generateFqn)
                .forEach(annotation -> builder.annotations('@' + annotation));

        pre.text()
    }
}
