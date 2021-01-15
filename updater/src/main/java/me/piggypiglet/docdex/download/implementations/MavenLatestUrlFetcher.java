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
import org.jsoup.Jsoup;

import java.io.EOFException;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Function;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MavenLatestUrlFetcher extends JavadocDownloader<MavenLatestStrategy> {
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();
    private static final Map<UpdateStrategyType, Function<Versioning, String>> STRATEGY_VERSION_GETTERS = new EnumMap<>(UpdateStrategyType.class);

    static {
        STRATEGY_VERSION_GETTERS.put(UpdateStrategyType.MAVEN_LATEST, Versioning::getLatest);
        STRATEGY_VERSION_GETTERS.put(UpdateStrategyType.MAVEN_LATEST_RELEASE, Versioning::getRelease);
    }

    @NotNull
    @Override
    protected Set<URL> provideLatestUrl(final @NotNull MavenLatestStrategy strategy) {
        String url = strategy.getArtifactLink();

        if (!url.endsWith("/")) {
            url = url + '/';
        }

        final String finalUrl = url;
        final Set<URL> urls = new HashSet<>();
        final Metadata metadata = scan(finalUrl + "maven-metadata.xml");

        if (metadata == null) {
            return urls;
        }

        final String artifact = metadata.getArtifactId();
        final String version = STRATEGY_VERSION_GETTERS.get(strategy.getType()).apply(metadata.getVersioning());
        final String javadocUrl = finalUrl + version + '/' + artifact + '-' + version + "-javadoc.jar";

        try {
            urls.add(new URL(javadocUrl));
        } catch (MalformedURLException exception) {
            LOGGER.info("Something is wrong with the url: " + javadocUrl, exception);
        }

        if (strategy.getType() == UpdateStrategyType.MAVEN_LATEST) {
            Optional.ofNullable(scan(finalUrl + version + "/maven-metadata.xml"))
                    .map(Metadata::getVersioning)
                    .map(Versioning::getSnapshot)
                    .ifPresent(snapshot -> {
                        final String start = finalUrl + version + '/';
                        final String snapshotVersion = version.replace("-SNAPSHOT", "") + '-' + snapshot.getTimestamp() + '-' + snapshot.getBuildNumber();
                        final String jar = artifact + '-' + snapshotVersion + "-javadoc.jar";

                        try {
                            urls.add(new URL(start + jar));
                            urls.add(new URL(start + snapshotVersion + '/' + jar));
                        } catch (MalformedURLException exception) {
                            LOGGER.error("Something when wrong with a url", exception);
                        }
                    });
        }

        return urls;
    }

    @Nullable
    private static Metadata scan(@NotNull final String url) {
        final HttpRequest request;

        try {
            request = HttpRequest.newBuilder(new URL(url).toURI()).build();
        } catch (MalformedURLException | URISyntaxException exception) {
            LOGGER.error(url + " is invalid.", exception);
            return null;
        }

        Metadata metadata;

        try {
            final String input = CLIENT.send(request, HttpResponse.BodyHandlers.ofString()).body()
                    .replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n", "")
                    .trim();
            final MetadataXpp3Reader xmlReader = new MetadataXpp3Reader();

            try (StringReader reader = new StringReader(input)) {
                metadata = xmlReader.read(reader);
            } catch (EOFException exception) {
                LOGGER.error("Something went really wrong with " + url, exception);
                return null;
            } catch (XmlPullParserException exception) {
                try (StringReader stringReader = new StringReader(Jsoup.parse(input, url)
                        .selectFirst("body > div.pretty-print > .folder")
                        .text())) {
                    metadata = xmlReader.read(stringReader);
                } catch (XmlPullParserException exception1) {
                    LOGGER.error("Something went wrong when connecting to & parsing " + url, exception);
                    return null;
                }
            }
        } catch (InterruptedException | IOException exception) {
            LOGGER.error("Something went wrong when connecting to & parsing " + url, exception);
            return null;
        }

        return metadata;
    }
}
