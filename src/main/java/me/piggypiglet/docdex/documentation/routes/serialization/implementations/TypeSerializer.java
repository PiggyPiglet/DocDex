package me.piggypiglet.docdex.documentation.routes.serialization.implementations;

import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.objects.method.MethodMetadata;
import me.piggypiglet.docdex.documentation.objects.type.TypeMetadata;
import me.piggypiglet.docdex.documentation.routes.serialization.JsonSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TypeSerializer implements JsonSerializer {
    private static final Set<DocumentedTypes> TYPES = Set.of(
            DocumentedTypes.CLASS, DocumentedTypes.INTERFACE,
            DocumentedTypes.ANNOTATION, DocumentedTypes.ENUM
    );

    private static final String METHODS_KEY = "methods";
    private static final Function<TypeMetadata, Set<DocumentedObject>> METHODS_GETTER = TypeMetadata::getMethods;

    @Override
    public boolean shouldSerialize(final @NotNull DocumentedObject object) {
        return TYPES.contains(object.getType());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void serialize(final @NotNull DocumentedObject object, final @NotNull Map<String, Object> map) {
        final TypeMetadata metadata = (TypeMetadata) object.getMetadata();
        final Map<String, Object> rawMetadata = (Map<String, Object>) map.get("metadata");

        rawMetadata.put(METHODS_KEY, METHODS_GETTER.apply(metadata).stream()
                .map(obj -> {
                    final MethodMetadata methodMetadata = (MethodMetadata) obj.getMetadata();

                    return methodMetadata.getPackage() + '.' + methodMetadata.getOwner() + '#' + obj.getName();
                })
                .collect(Collectors.toSet()));
    }
}
