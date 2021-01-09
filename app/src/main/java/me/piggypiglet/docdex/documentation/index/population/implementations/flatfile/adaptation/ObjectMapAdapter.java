package me.piggypiglet.docdex.documentation.index.population.implementations.flatfile.adaptation;

import com.google.gson.*;
import com.google.inject.util.Types;
import me.piggypiglet.docdex.documentation.index.objects.DocumentedObjectKey;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.utils.ParameterTypes;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ObjectMapAdapter implements JsonSerializer<Map<DocumentedObjectKey, DocumentedObject>>, JsonDeserializer<Map<DocumentedObjectKey, DocumentedObject>> {
    public static final Type DESERIALIZED_TYPE = Types.mapOf(DocumentedObjectKey.class, DocumentedObject.class);

    private static final String DELIMITER = ";";
    private static final Pattern KEY_DELIMITER = Pattern.compile(";");
    private static final Type SERIALIZED_TYPE = Types.mapOf(String.class, DocumentedObject.class);

    @NotNull
    @Override
    public JsonElement serialize(@NotNull final Map<DocumentedObjectKey, DocumentedObject> src, @NotNull final Type typeOfSrc,
                                 @NotNull final JsonSerializationContext context) {
        return context.serialize(src.entrySet().stream()
                .collect(Collectors.toMap(entry -> {
                    final DocumentedObjectKey key = entry.getKey();
                    final Map<ParameterTypes, String> params = key.getParams();

                    return new StringJoiner(DELIMITER)
                            .add(key.getName())
                            .add(key.getFqn())
                            .add(params.get(ParameterTypes.FULL) + ' ')
                            .add(params.get(ParameterTypes.TYPE) + ' ')
                            .add(params.get(ParameterTypes.NAME) + ' ');
                }, Map.Entry::getValue)));
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Map<DocumentedObjectKey, DocumentedObject> deserialize(@NotNull final JsonElement json, @NotNull final Type typeOfT,
                                                                  @NotNull final JsonDeserializationContext context) {
        return ((Map<String, DocumentedObject>) context.deserialize(json, SERIALIZED_TYPE)).entrySet().stream()
                .collect(Collectors.toMap(entry -> {
                    final String[] parts = KEY_DELIMITER.split(entry.getKey());

                    return new DocumentedObjectKey(
                            parts[0], parts[1],
                            Map.of(
                                    ParameterTypes.FULL, offTheEnd(parts[2]),
                                    ParameterTypes.TYPE, offTheEnd(parts[3]),
                                    ParameterTypes.NAME, offTheEnd(parts[4])
                            )
                    );
                }, Map.Entry::getValue));
    }

    @NotNull
    private static String offTheEnd(@NotNull final String string) {
        return string.substring(0, string.length() - 1);
    }
}
