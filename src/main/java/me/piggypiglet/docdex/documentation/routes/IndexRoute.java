package me.piggypiglet.docdex.documentation.routes;

import com.google.common.collect.Multimap;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.util.Types;
import me.piggypiglet.docdex.documentation.index.DocumentationIndex;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.util.PotentialObject;
import me.piggypiglet.docdex.documentation.routes.serialization.JsonSerializer;
import me.piggypiglet.docdex.documentation.routes.serialization.implementations.PotentialObjectSerializer;
import me.piggypiglet.docdex.http.request.Request;
import me.piggypiglet.docdex.http.route.json.JsonRoute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class IndexRoute extends JsonRoute {
    private final DocumentationIndex index;
    private final Set<JsonSerializer> serializers;
    private final Gson gson = provideGson();

    @Inject
    public IndexRoute(@NotNull final DocumentationIndex index, @NotNull @Named("json serializers") final Set<JsonSerializer> serializers) {
        super("index");
        this.index = index;
        this.serializers = serializers;
    }

    @Nullable
    @Override
    protected Object respond(@NotNull final Request request) {
        final Multimap<String, String> params = request.getParams();
        final String javadoc = params.get("javadoc").stream().findAny().orElse(null);
        final String query = params.get("query").stream().findAny()
                .map(str -> str.replace("~", "#"))
                .orElse(null);

        if (javadoc == null || query == null) {
            return null;
        }

        final DocumentedObject object = index.get(javadoc, query);

        if (object == null) {
            return null;
        }

        final Map<String, Object> map = gson.fromJson(gson.toJsonTree(object), Types.mapOf(String.class, Object.class));

        serializers.stream()
                .filter(serializer -> serializer.shouldSerialize(object))
                .findAny()
                .ifPresent(serializer -> serializer.serialize(object, map));

        return map;
    }

    @NotNull
    @Override
    protected Gson provideGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(PotentialObject.class, new PotentialObjectSerializer())
                .create();
    }
}
