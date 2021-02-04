package me.piggypiglet.docdex.documentation;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.util.Types;
import me.piggypiglet.docdex.config.Config;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.objects.DocumentedObjectResult;
import me.piggypiglet.docdex.documentation.response.ObjectListResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@SuppressWarnings("unchecked")
public final class DocDexHttp {
    private static final Logger LOGGER = LoggerFactory.getLogger("DocDex HTTP");

    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int BAD_REQUEST = 400;
    private static final int BAD_GATEWAY = 502;

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private static final Type JAVADOC_SET = Types.setOf(Javadoc.class);
    private static final Type OBJECT_LIST = Types.listOf(DocumentedObjectResult.class);

    private final Config config;

    private final HttpRequest javadocsRequest;

    @Inject
    public DocDexHttp(@NotNull final Config config) {
        this.config = config;

        this.javadocsRequest = HttpRequest.newBuilder(URI.create(config.getUrl() + "/javadocs")).build();
    }

    @NotNull
    public Set<Javadoc> getJavadocs() {
        return Optional.ofNullable(response(javadocsRequest))
                .map(HttpResponse::body)
                .map(json -> (Set<Javadoc>) GSON.fromJson(json, JAVADOC_SET))
                .orElse(Collections.emptySet());
    }

    @Nullable
    public ObjectListResult getObjects(@NotNull final String uri) {
        final HttpRequest request = HttpRequest.newBuilder(URI.create(config.getUrl() + uri)).build();
        final HttpResponse<String> nullableResponse = response(request);

        return Optional.ofNullable(nullableResponse)
                .map(response -> {
                    final int statusCode = response.statusCode();
                    final String body = response.body();

                    if (statusCode == SERVICE_UNAVAILABLE || statusCode == BAD_REQUEST) {
                        return new ObjectListResult(null, body);
                    }

                    if (statusCode == BAD_GATEWAY) {
                        return new ObjectListResult(null, "DocDex is currently under maintenance.");
                    }

                    return new ObjectListResult(GSON.fromJson(body, OBJECT_LIST), "");
                })
                .orElse(null);
    }

    @Nullable
    private static HttpResponse<String> response(@NotNull final HttpRequest request) {
        try {
            return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException exception) {
            LOGGER.error("Interrupted.", exception);
            Thread.currentThread().interrupt();
            return null;
        } catch (IOException exception) {
            LOGGER.error("Something went wrong when connecting to " + request.uri(), exception);
            return null;
        }
    }
}
