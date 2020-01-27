package me.piggypiglet.helpdocs.utils;

import me.piggypiglet.helpdocs.data.Documentation;
import me.piggypiglet.helpdocs.mappers.DocumentationMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DataUtils {
    private static final Document HELPCHAT_DOCS;
    private static final DocumentationMapper DOCUMENTATION_MAPPER = new DocumentationMapper();

    static {
        try {
            HELPCHAT_DOCS = Jsoup.connect("https://helpch.at/docs").get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Documentation> getDocuments() {
        final Map<String, String> urls = HELPCHAT_DOCS.select(".jumbotron .list-group a").stream()
                .collect(Collectors.toMap(Element::text, e -> e.absUrl("href") + "/allclasses-noframe.html"));

        urls.forEach((ver, url) -> {
            try {
                DOCUMENTATION_MAPPER.generate(Jsoup.connect(url).get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return null;
    }
}
