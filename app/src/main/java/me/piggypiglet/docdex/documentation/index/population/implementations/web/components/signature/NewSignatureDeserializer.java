package me.piggypiglet.docdex.documentation.index.population.implementations.web.components.signature;

import me.piggypiglet.docdex.documentation.index.population.implementations.web.utils.DeserializationUtils;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.detail.DocumentedDetailBuilder;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

import java.util.Optional;

import static me.piggypiglet.docdex.documentation.index.population.implementations.web.components.signature.SignatureConstants.SPACE_DELIMITER;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class NewSignatureDeserializer {
    private NewSignatureDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    public static <R extends DocumentedObject.Builder<R> & DocumentedDetailBuilder<R>> void deserialize(@NotNull final Element details, @NotNull final R builder) {
        final Element signature = details.selectFirst(".memberSignature, .member-signature");
        Optional.ofNullable(signature.selectFirst(".modifiers"))
                .map(Element::text)
                .map(SPACE_DELIMITER::split)
                .ifPresent(builder::modifiers);
        Optional.ofNullable(signature.selectFirst(".annotations"))
                .map(annotations -> annotations.select("a").stream())
                .map(annotations -> annotations.filter(element -> element.text().startsWith("@")))
                .map(annotations -> annotations.map(element -> element.text(element.text().substring(1))))
                .map(annotations -> annotations.map(DeserializationUtils::generateFqn))
                .ifPresent(annotations -> annotations.forEach(annotation -> builder.annotations('@' + annotation)));
        Optional.ofNullable(signature.selectFirst(".returnType, .return-type")).ifPresent(returnType ->
                builder.returns(returnType.text()));
    }
}
