package me.piggypiglet.docdex.documentation.index.data.population.implementations.web.components.method;

import me.piggypiglet.docdex.documentation.index.data.population.implementations.web.utils.DeserializationUtils;
import me.piggypiglet.docdex.documentation.objects.method.DocumentedMethodBuilder;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

import java.util.Arrays;
import java.util.Optional;

import static me.piggypiglet.docdex.documentation.index.data.population.implementations.web.components.method.MethodDeserializer.LINE_DELIMITER;
import static me.piggypiglet.docdex.documentation.index.data.population.implementations.web.components.method.MethodDeserializer.LIST_DELIMITER;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class NewSignatureDeserializer {
    private NewSignatureDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    static void deserialize(@NotNull final Element details, @NotNull final DocumentedMethodBuilder builder) {
        final Element signature = details.selectFirst(".memberSignature");
        Optional.ofNullable(signature.selectFirst(".annotations"))
                .map(annotations -> annotations.select("a").stream())
                .map(annotations -> annotations.map(element -> element.text(element.text().substring(1))))
                .map(annotations -> annotations.map(DeserializationUtils::generateFqn))
                .ifPresent(annotations -> annotations.forEach(annotation -> builder.annotations('@' + annotation)));
        Optional.ofNullable(signature.selectFirst(".returnType")).ifPresent(returnType ->
                builder.returns(returnType.text()));
        Optional.ofNullable(signature.selectFirst(".arguments")).ifPresent(arguments ->
                Arrays.stream(LIST_DELIMITER.split(LINE_DELIMITER.matcher(arguments.text().replace(")", "")).replaceAll(" ")))
                        .map(String::trim)
                        .forEach(builder::parameters));
    }
}
