package me.piggypiglet.docdex.documentation.index.data.population.implementations.web.components.details.signature;

import java.util.regex.Pattern;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
final class SignatureConstants {
    public static final Pattern SPACE_DELIMITER = Pattern.compile("[ \u00A0]");

    private SignatureConstants() {
        throw new AssertionError("This class cannot be instantiated.");
    }
}
