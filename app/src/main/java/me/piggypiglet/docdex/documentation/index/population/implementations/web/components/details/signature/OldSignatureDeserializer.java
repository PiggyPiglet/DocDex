package me.piggypiglet.docdex.documentation.index.population.implementations.web.components.details.signature;

import me.piggypiglet.docdex.documentation.index.population.implementations.web.utils.DeserializationUtils;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.detail.DocumentedDetailBuilder;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.piggypiglet.docdex.documentation.index.population.implementations.web.components.details.signature.SignatureConstants.SPACE_DELIMITER;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class OldSignatureDeserializer {
    private static final Pattern ANNOTATION_PATTERN = Pattern.compile("@.+?(?=\\n|(?<!,) )");

    private OldSignatureDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    public static <R extends DocumentedObject.Builder<R> & DocumentedDetailBuilder<R>> void deserialize(@NotNull final Element details, @NotNull final R builder,
                                                                                                        @NotNull final String name) {
        final Element pre = details.selectFirst("pre");
        String replacedPre = pre.text()
                .replace(name + '(', "\\")
                .replace(name + "\u200b(", "\\");

        if (!replacedPre.contains("\\")) {
            replacedPre = replacedPre.replace(' ' + name, "\\")
                    .replace('\u00a0' + name, "\\");
        }

        final Matcher annotationMatcher = ANNOTATION_PATTERN.matcher(replacedPre.substring(0, replacedPre.lastIndexOf('\\')));
        final Set<String> annotations = new HashSet<>();

        while (annotationMatcher.find()) {
            annotations.add(annotationMatcher.group());
        }

        final AtomicReference<String> preTextReference = new AtomicReference<>(pre.text());

        pre.select("a").stream()
                .filter(anchor -> anchor.hasAttr("title"))
                .forEach(anchor -> annotations.stream()
                        .filter(annotation -> {
                            if (annotation.contains("(")) {
                                return annotation.substring(0, annotation.indexOf('(')).equals(anchor.text());
                            }

                            return annotation.equals(anchor.text());
                        })
                        .findAny().ifPresent(annotation -> {
                            preTextReference.set(preTextReference.get()
                                    .replace(annotation + '\n', "")
                                    .replace(annotation + ' ', ""));
                            builder.annotations(annotation.replace(
                                    anchor.text().substring(1),
                                    DeserializationUtils.generateFqn(anchor).replace("@", "")
                            ));
                        }));

        final String preText = preTextReference.get().trim();
        final String[] preSplit = SPACE_DELIMITER.split(preText);
        final String lowerName = name.toLowerCase();

        int lastModifierIndex = 0;
        for (int i = 0; i < preSplit.length; ++i) {
            final boolean isMethod = preSplit[i].toLowerCase().startsWith(lowerName + '(') ||
                    preSplit[i].toLowerCase().startsWith(lowerName + "\u200b(");
            final boolean isField = preSplit[i].toLowerCase().equals(lowerName) &&
                    i == preSplit.length - 1;

            if (isMethod || isField) {
                builder.returns(preSplit[i - 1]);
                lastModifierIndex = i - 2;
                break;
            }
        }

        for (int i = 0; i <= lastModifierIndex; ++i) {
            builder.modifiers(preSplit[i]);
        }
    }
}
