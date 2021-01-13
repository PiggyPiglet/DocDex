package me.piggypiglet.docdex.download.implementations;

import me.piggypiglet.docdex.config.UpdateStrategyType;
import me.piggypiglet.docdex.config.strategies.maven.MavenLatestStrategy;
import me.piggypiglet.docdex.download.framework.JavadocDownloader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MavenLatestUrlFetcher extends JavadocDownloader<MavenLatestStrategy> {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Map<UpdateStrategyType, String> STRATEGY_TAG_NAMES = new EnumMap<>(UpdateStrategyType.class);

    static {
        STRATEGY_TAG_NAMES.put(UpdateStrategyType.MAVEN_LATEST, "latest");
        STRATEGY_TAG_NAMES.put(UpdateStrategyType.MAVEN_LATEST_RELEASE, "release");
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
            final Document document = DocumentBuilderFactory.newDefaultInstance()
                    .newDocumentBuilder()
                    .parse(input);

            final Element metadata = (Element) getFirst(document.getDocumentElement(), "metadata");
            final Element versioning = (Element) getFirst(metadata, "versioning");

            final String artifact = getFirst(metadata, "artifactId").getTextContent();
            final String version = getFirst(versioning, STRATEGY_TAG_NAMES.get(strategy.getStrategy())).getTextContent();

            return new URL(url + version + '/' + artifact + '-' + version + ".jar");
        } catch (InterruptedException | IOException | ParserConfigurationException | SAXException exception) {
            LOGGER.error("Something went wrong when connecting to & parsing " + url, exception);
            return null;
        }
    }

    @NotNull
    private Node getFirst(@NotNull final Element element, @NotNull final String tag) {
        return element.getElementsByTagName(tag).item(0);
    }
}
