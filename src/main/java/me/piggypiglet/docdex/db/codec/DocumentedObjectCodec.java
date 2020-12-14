package me.piggypiglet.docdex.db.codec;

import com.google.gson.Gson;
import com.google.inject.util.Types;
import com.mongodb.MongoClient;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.objects.method.MethodMetadata;
import me.piggypiglet.docdex.documentation.objects.type.TypeMetadata;
import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DocumentedObjectCodec implements CollectibleCodec<DocumentedObject> {
    private static final Codec<Document> DOCUMENT_CODEC = MongoClient.getDefaultCodecRegistry().get(Document.class);
    private static final Gson GSON = new Gson();

    @NotNull
    @Override
    public DocumentedObject generateIdIfAbsentFromDocument(@NotNull final DocumentedObject document) {
        return document;
    }

    @Override
    public boolean documentHasId(@NotNull final DocumentedObject document) {
        return true;
    }

    @Override
    public BsonValue getDocumentId(@NotNull final DocumentedObject document) {
        final String packaj;

        if (document.getType() == DocumentedTypes.METHOD) {
            packaj = ((MethodMetadata) document.getMetadata()).getPackage();
        } else {
            packaj = ((TypeMetadata) document.getMetadata()).getPackage();
        }

        return new BsonString(packaj + '.' + document.getName());
    }

    @Override
    public DocumentedObject decode(final BsonReader reader, final DecoderContext decoderContext) {
        return GSON.fromJson(GSON.toJsonTree(DOCUMENT_CODEC.decode(reader, decoderContext)), DocumentedObject.class);
    }

    @Override
    public void encode(final BsonWriter writer, final DocumentedObject value, final EncoderContext encoderContext) {
        DOCUMENT_CODEC.encode(writer, new Document(GSON.fromJson(GSON.toJsonTree(value), Types.mapOf(String.class, Object.class))),
                encoderContext);
    }

    @Override
    public Class<DocumentedObject> getEncoderClass() {
        return DocumentedObject.class;
    }
}
