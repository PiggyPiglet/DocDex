package me.piggypiglet.helpdocs.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Map;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DataUtils {
    private static final Document HELPCHAT_DOCS;

    static {
        try {
            HELPCHAT_DOCS = Jsoup.connect("https://helpch.at/docs").get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> getUrls() {
        return HELPCHAT_DOCS.select(".jumbotron .list-group").stream()
                .collect(Collectors.toMap(Element::text, e -> e.absUrl("href")));
    }
}
