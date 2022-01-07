package me.piggypiglet.docdex.documentation.index.population.implementations.web;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.piggypiglet.docdex.documentation.index.population.implementations.web.components.TypeDeserializer;
import me.piggypiglet.docdex.documentation.index.population.implementations.web.components.field.FieldDeserializer;
import me.piggypiglet.docdex.documentation.index.population.implementations.web.components.method.MethodDeserializer;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.type.TypeMetadata;
import me.piggypiglet.docdex.documentation.utils.DataUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JavadocPageDeserializer {
    private static final Map<Set<String>, String> NEW_DETAIL_CLASSES = Map.of(
            Set.of(".constructor-details", ".constructorDetails"), "methods",
            Set.of(".method-details", ".methodDetails"), "methods",
            Set.of(".constant-details", ".constantDetails"), "fields",
            Set.of(".field-details", ".fieldDetails"), "fields"
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
    public static Set<DocumentedObject> deserialize(@NotNull final Document document, @NotNull final String link) {
        final Set<DocumentedObject> objects = new HashSet<>();

        final DocumentedObject type = TypeDeserializer.deserialize(
                document.selectFirst(".description, .class-description"),
                link,
                document.selectFirst(".header > .title").previousElementSibling()
        );
        objects.add(type);

        final Element possibleElements = document.selectFirst(".method-details, .methodDetails");
        final boolean old = possibleElements == null;

        final Multimap<String, Map.Entry<String, Element>> detailElements = HashMultimap.create();

        if (old) {
            document.select(".details > ul.blockList > li.blockList ul.blockList > li.blockList > h3").stream()
                    .filter(element -> OLD_DETAIL_HEADERS.containsKey(element.text().toLowerCase()))
                    .forEach(element -> {
                        final String key = OLD_DETAIL_HEADERS.get(element.text().toLowerCase());

                        element.parent().select("ul.blockList,ul.blockListLast").forEach(ul -> {
                            String name = ul.previousElementSibling().attr("name");
                            name = name.isBlank() ? ul.previousElementSibling().id() : name;

                            detailElements.put(key, Map.entry(link + '#' + name, ul.selectFirst("li.blockList")));
                        });
                    });
        } else {
            NEW_DETAIL_CLASSES.forEach((classes, key) ->
                document.select(classes.stream().map(clazz -> clazz + " > ul > li").collect(Collectors.joining(","))).forEach(block ->
                            detailElements.put(key, Map.entry(link + '#' + Optional.ofNullable(block.selectFirst(".detail > h3 > a")).orElse(block.selectFirst(".detail")).id(), block))
                )
            );
        }

        final String packaj = type.getPackage();
        final String owner = type.getName();
        final TypeMetadata metadata = (TypeMetadata) type.getMetadata();

        TYPE_MEMBER_FUNCTIONS.forEach((key, functions) -> {
            final Set<String> typeMembers = functions.getMemberGetter().apply(metadata);

            detailElements.get(key).stream()
                    .map(entry -> functions.getDeserializer().deserialize(entry.getValue(), entry.getKey(), packaj, owner, old))
                    .forEach(object -> {
                        objects.add(object);
                        typeMembers.add(DataUtils.getFqn(object));
                    });
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
        DocumentedObject deserialize(@NotNull final Element element, @NotNull final String link,
                                     @NotNull final String packaj, @NotNull final String owner,
                                     final boolean old);
    }
}
