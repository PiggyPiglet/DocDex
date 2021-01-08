package me.piggypiglet.docdex.http.request;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import fi.iki.elonen.NanoHTTPD;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class Request {
    private final String uri;
    private final Multimap<String, String> params;
    private final Map<String, String> headers;
    private final String requester;

    private Request(@NotNull final String uri, @NotNull final Multimap<String, String> params,
                    @NotNull final Map<String, String> headers, @NotNull final String requester) {
        this.uri = uri;
        this.params = params;
        this.headers = headers;
        this.requester = requester;
    }

    @NotNull
    public static Request of(@NotNull final String uri, @NotNull final Multimap<String, String> params,
                             @NotNull final Map<String, String> headers, @NotNull final String requester) {
        return new Request(uri, params, headers, requester);
    }

    @NotNull
    public static Request from(@NotNull final NanoHTTPD.IHTTPSession nanoHttpdRequest) {
        return new Request(
                nanoHttpdRequest.getUri(),
                nanoHttpdRequest.getParameters().entrySet().stream()
                        .collect(
                                ArrayListMultimap::create,
                                (map, entry) -> map.putAll(entry.getKey(), entry.getValue()),
                                (map1, map2) -> map1.putAll(map2)
                        ),
                nanoHttpdRequest.getHeaders(),
                nanoHttpdRequest.getRemoteIpAddress()
        );
    }

    @NotNull
    public String getUri() {
        return uri;
    }

    @NotNull
    public Multimap<String, String> getParams() {
        return params;
    }

    @NotNull
    public Map<String, String> getHeaders() {
        return headers;
    }

    @NotNull
    public String getRequester() {
        return requester;
    }
}
