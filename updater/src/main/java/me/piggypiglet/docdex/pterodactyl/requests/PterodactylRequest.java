package me.piggypiglet.docdex.pterodactyl.requests;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.piggypiglet.docdex.config.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class PterodactylRequest {
    protected static final Logger LOGGER = LoggerFactory.getLogger("Pterodactyl");

    private static final Map.Entry<String, String> CONTENT_TYPE_HEADER = Map.entry("Content-Type", "application/json");
    private static final Map.Entry<String, String> ACCEPT_HEADER = Map.entry("Accept", "Application/vnd.pterodactyl.v1+json");
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private final Config config;
    private final Map<String, String> headers;

    protected PterodactylRequest(@NotNull final Config config) {
        this.config = config;
        this.headers = getHeaders();
    }

    @NotNull
    @Unmodifiable
    protected Map<String, String> getHeaders() {
        return Map.ofEntries(
                Map.entry("Authorization", "Bearer " + config.getPterodactyl().getToken()),
                CONTENT_TYPE_HEADER,
                ACCEPT_HEADER
        );
    }

    @NotNull
    protected <T> HttpResponse<T> get(@NotNull final String endpoint, @NotNull final Class<T> type)
            throws IOException, InterruptedException, URISyntaxException {
        final HttpRequest request = builder(endpoint)
                .GET()
                .build();

        return CLIENT.send(request, new JsonBodyHandler<>(type));
    }

    @NotNull
    protected HttpResponse<String> post(@NotNull final String endpoint, @NotNull final Object payload)
            throws IOException, URISyntaxException, InterruptedException {
        final HttpRequest request = builder(endpoint)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(payload), StandardCharsets.UTF_8))
                .build();

        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    @NotNull
    private HttpRequest.Builder builder(@NotNull final String endpoint) throws MalformedURLException, URISyntaxException {
        final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(new URL(config.getPterodactyl().getLink() + endpoint).toURI());

        headers.forEach(requestBuilder::setHeader);

        return requestBuilder;
    }

    private static final class JsonBodyHandler<T> implements HttpResponse.BodyHandler<T> {
        private final Type type;

        private JsonBodyHandler(@NotNull final Type type) {
            this.type = type;
        }

        @Override
        public HttpResponse.BodySubscriber<T> apply(final HttpResponse.ResponseInfo responseInfo) {
            return HttpResponse.BodySubscribers.mapping(
                    HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8),
                    json -> GSON.fromJson(json, type)
            );
        }
    }
}
