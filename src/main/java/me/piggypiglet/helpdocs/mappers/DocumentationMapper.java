package me.piggypiglet.helpdocs.mappers;

import me.piggypiglet.helpdocs.data.Documentation;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DocumentationMapper implements Mapper<Documentation> {


    @Override
    public Documentation generate(Element element) {
        final Elements elements = element.select(".contentContainer dl .memberNameLink a");
        
        return null;
    }
}
