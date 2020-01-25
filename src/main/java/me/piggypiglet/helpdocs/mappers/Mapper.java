package me.piggypiglet.helpdocs.mappers;

import org.jsoup.nodes.Element;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public interface Mapper<T> {
    T generate(Element element);
}
