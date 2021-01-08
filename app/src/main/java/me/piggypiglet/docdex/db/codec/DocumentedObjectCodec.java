package me.piggypiglet.docdex.db.codec;

import com.google.gson.Gson;
import com.google.inject.util.Types;
import com.mongodb.MongoClient;
import me.piggypiglet.docdex.db.objects.MongoDocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
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
public final class DocumentedObjectCodec implements CollectibleCodec<MongoDocumentedObject> {
    private static final Codec<Document> DOCUMENT_CODEC = MongoClient.getDefaultCodecRegistry().get(Document.class);
    private static final Gson GSON = new Gson();

    @NotNull
    @Override
    public MongoDocumentedObject generateIdIfAbsentFromDocument(@NotNull final MongoDocumentedObject document) {
        return document;
    }

    @Override
    public boolean documentHasId(@NotNull final MongoDocumentedObject document) {
        return true;
    }

    @Override
    public BsonValue getDocumentId(@NotNull final MongoDocumentedObject document) {
        return new BsonString(document.getIdentifier());
    }

    @Override
    public MongoDocumentedObject decode(final BsonReader reader, final DecoderContext decoderContext) {
        final Document document = DOCUMENT_CODEC.decode(reader, decoderContext);

        return MongoDocumentedObject.builder(GSON.fromJson(GSON.toJsonTree(document.get("object")), DocumentedObject.class))
                .identifier(document.getString("identifier"))
                .name(document.getString("name"))
                .fqn(document.getString("fqn"))
                .fullParams(document.getString("fullParams"))
                .typeParams(document.getString("typeParams"))
                .fqnTypeParams(document.getString("fqnTypeParams"))
                .nameParams(document.getString("nameParams"))
                .fqnNameParams(document.getString("fqnNameParams"))
                .build();
    }

    @Override
    public void encode(final BsonWriter writer, final MongoDocumentedObject value, final EncoderContext encoderContext) {
        DOCUMENT_CODEC.encode(writer, new Document(GSON.fromJson(GSON.toJsonTree(value), Types.mapOf(String.class, Object.class))),
                encoderContext);
    }

    @Override
    public Class<MongoDocumentedObject> getEncoderClass() {
        return MongoDocumentedObject.class;
    }
}
