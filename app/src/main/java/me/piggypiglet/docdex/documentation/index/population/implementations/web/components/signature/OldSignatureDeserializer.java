package me.piggypiglet.docdex.documentation.index.population.implementations.web.components.signature;

import me.piggypiglet.docdex.documentation.index.population.implementations.web.utils.DeserializationUtils;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.detail.DocumentedDetailBuilder;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static me.piggypiglet.docdex.documentation.index.population.implementations.web.components.signature.SignatureConstants.SPACE_DELIMITER;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class OldSignatureDeserializer {
    private OldSignatureDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    public static <R extends DocumentedObject.Builder<R> & DocumentedDetailBuilder<R>> void deserialize(@NotNull final Element details, @NotNull final R builder,
                                                                                                        @NotNull final String name) {
        final Element pre = details.selectFirst("pre");
        String replacedPre = pre.text()
                .replace(' ' + name + '(', "汉")
                .replace('\u00a0' + name + '(', "汉")
                .replace(' ' + name + '\u200b' + '(', "汉")
                .replace('\u00a0' + name + '\u200b' + '(', "汉");

        if (!replacedPre.contains("汉")) {
            replacedPre = replacedPre.replace(' ' + name, "汉")
                    .replace('\u00a0' + name, "汉");
        }

        final Set<String> annotations = new HashSet<>();

        boolean isAnnotation = false;
        int count = 0;
        StringBuilder annotationBuilder = new StringBuilder();
        for (int i = 0; i < replacedPre.length(); ++i) {
            final char character = replacedPre.charAt(i);

            if (character == '汉') {
                break;
            }

            if (character != '@' && !isAnnotation) {
                continue;
            }

            isAnnotation = true;

            if (character == '(') {
                ++count;
            }

            annotationBuilder.append(character);

            if ((character == ')' && --count == 0)
                    || ((count == 0 && !Character.isJavaIdentifierPart(character) && character != '@'))) {
                isAnnotation = false;
                annotations.add(annotationBuilder.toString().trim());
                annotationBuilder.setLength(0);
            }
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

        annotations.forEach(annotation -> {
            if (builder.getAnnotations().stream()
                    .noneMatch(storedAnnotation -> storedAnnotation.toLowerCase().replace("@", "").contains(annotation.toLowerCase().replace("@", "")))) {
                builder.annotations(annotation);
            }
        });

        String preText = replacedPre;

        for (final String annotation : annotations) {
            preText = preText.replace(annotation, "");
        }

        preText = preText.trim();

        final String[] preSplit = SPACE_DELIMITER.split(preText);
        final String lowerName = name.toLowerCase();

        int lastModifierIndex = 0;
        for (int i = 0; i < preSplit.length; ++i) {
            final String lowerPreSplit = preSplit[i].toLowerCase();
            final boolean isMethod = lowerPreSplit.startsWith(lowerName + '(') ||
                    lowerPreSplit.startsWith(lowerName + "\u200b(");
            final boolean isField = preSplit[i].equalsIgnoreCase(lowerName) &&
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
