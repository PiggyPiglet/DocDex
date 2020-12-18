package me.piggypiglet.docdex.documentation.index.data.population.implementations.web;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.piggypiglet.docdex.documentation.index.data.population.implementations.web.components.TypeDeserializer;
import me.piggypiglet.docdex.documentation.index.data.population.implementations.web.components.details.field.FieldDeserializer;
import me.piggypiglet.docdex.documentation.index.data.population.implementations.web.components.details.method.MethodDeserializer;
import me.piggypiglet.docdex.documentation.index.data.utils.DataUtils;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.type.TypeMetadata;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JavadocPageDeserializer {
    private static final Map<String, String> NEW_DETAIL_CLASSES = Map.of(
            ".constructorDetails", "methods",
            ".methodDetails", "methods",
            ".constantDetails", "fields",
            ".fieldDetails", "fields"
    );

    private static final Map<String, String> OLD_DETAIL_HEADERS = Map.of(
            "constructor detail", "methods",
            "method detail", "methods",
            "enum constant detail", "fields",
            "field detail", "fields"
    );

    private static final Map<String, TypeMemberFunctions> TYPE_MEMBER_FUNCTIONS = Map.of(
            "methods", new TypeMemberFunctions(MethodDeserializer::deserialize, TypeMetadata::getMethods),
            "fields", new TypeMemberFunctions(FieldDeserializer::deserialize, TypeMetadata::getFields)
    );

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
        final Element possibleElements = document.selectFirst(".methodDetails ul.blockList > li.blockList");
        final boolean old = possibleElements == null;

        final Multimap<String, Element> detailElements = HashMultimap.create();

        if (old) {
            document.select(".details > ul.blockList > li.blockList > ul.blockList > li.blockList > h3").stream()
                    .filter(element -> OLD_DETAIL_HEADERS.containsKey(element.text().toLowerCase()))
                    .forEach(element -> detailElements.putAll(
                            OLD_DETAIL_HEADERS.get(element.text().toLowerCase()),
                            element.parent().select("ul > li.blockList")
                    ));
        } else {
            NEW_DETAIL_CLASSES.forEach((clazz, key) ->
                    detailElements.putAll(key, document.select(clazz + " ul.blockList > li.blockList")));
        }

        final String packaj = type.getPackage();
        final String owner = type.getName();
        final TypeMetadata metadata = (TypeMetadata) type.getMetadata();

        TYPE_MEMBER_FUNCTIONS.forEach((key, functions) -> {
            final Set<String> typeMembers = functions.getMemberGetter().apply(metadata);
            detailElements.get(key).stream()
                    .map(element -> functions.getDeserializer().deserialize(element, packaj, owner, old))
                    .peek(objects::add)
                    .map(DataUtils::getFqn)
                    .forEach(typeMembers::add);
        });

        return objects;
    }

    private static final class TypeMemberFunctions {
        private final TypeMemberDeserializer deserializer;
        private final Function<TypeMetadata, Set<String>> memberGetter;

        private TypeMemberFunctions(@NotNull final TypeMemberDeserializer deserializer,
                                    @NotNull final Function<TypeMetadata, Set<String>> memberGetter) {
            this.deserializer = deserializer;
            this.memberGetter = memberGetter;
        }

        @NotNull
        public TypeMemberDeserializer getDeserializer() {
            return deserializer;
        }

        @NotNull
        public Function<TypeMetadata, Set<String>> getMemberGetter() {
            return memberGetter;
        }
    }

    private interface TypeMemberDeserializer {
        DocumentedObject deserialize(@NotNull final Element element, @NotNull final String packaj,
                                     @NotNull final String owner, final boolean old);
    }
}
