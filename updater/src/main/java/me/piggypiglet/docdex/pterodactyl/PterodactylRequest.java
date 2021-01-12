package me.piggypiglet.docdex.pterodactyl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class PterodactylRequest {
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final Map.Entry<String, String> CONTENT_TYPE_HEADER = Map.entry("Content-Type", "application/json");
    private static final Map.Entry<String, String> ACCEPT_HEADER = Map.entry("Accept", "Application/vnd.pterodactyl.v1+json");

    private final String apiKey;

    protected PterodactylRequest(@NotNull final String apiKey) {
        this.apiKey = apiKey;
    }

    @NotNull
    @Unmodifiable
    public Map<String, String> getHeaders() {
        return Map.ofEntries(
                Map.entry(AUTHORIZATION_HEADER_KEY, "Bearer " + apiKey),
                CONTENT_TYPE_HEADER,
                ACCEPT_HEADER
        );
    }
}
