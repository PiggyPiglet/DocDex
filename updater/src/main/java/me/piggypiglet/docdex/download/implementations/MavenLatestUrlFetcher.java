package me.piggypiglet.docdex.download.implementations;

import me.piggypiglet.docdex.config.UpdateStrategyType;
import me.piggypiglet.docdex.config.strategies.maven.MavenLatestStrategy;
import me.piggypiglet.docdex.download.framework.JavadocDownloader;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MavenLatestUrlFetcher extends JavadocDownloader<MavenLatestStrategy> {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Map<UpdateStrategyType, Function<Versioning, String>> STRATEGY_VERSION_GETTERS = new EnumMap<>(UpdateStrategyType.class);

    static {
        STRATEGY_VERSION_GETTERS.put(UpdateStrategyType.MAVEN_LATEST, Versioning::getLatest);
        STRATEGY_VERSION_GETTERS.put(UpdateStrategyType.MAVEN_LATEST_RELEASE, Versioning::getRelease);
    }

    @Nullable
    @Override
    protected URL provideLatestUrl(final @NotNull MavenLatestStrategy strategy) {
        String url = strategy.getArtifactLink();

        if (!url.endsWith("/")) {
            url = url + '/';
        }

        final String metadataUrl = url + "maven-metadata.xml";

        final URI uri;

        try {
            uri = new URL(metadataUrl).toURI();
        } catch (MalformedURLException | URISyntaxException exception) {
            LOGGER.error(metadataUrl + " is invalid.", exception);
            return null;
        }

        final HttpRequest request = HttpRequest.newBuilder(uri).build();

        try (InputStream input = CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream()).body()) {
            final Metadata metadata = new MetadataXpp3Reader().read(input);

            final String artifact = metadata.getArtifactId();
            final String version = STRATEGY_VERSION_GETTERS.get(strategy.getType()).apply(metadata.getVersioning());

            return new URL(url + version + '/' + artifact + '-' + version + "-javadoc" + ".jar");
        } catch (InterruptedException | IOException | XmlPullParserException exception) {
            LOGGER.error("Something went wrong when connecting to & parsing " + url, exception);
            return null;
        }
    }
}
