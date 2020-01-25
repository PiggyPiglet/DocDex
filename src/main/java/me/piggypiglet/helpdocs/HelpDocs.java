package me.piggypiglet.helpdocs;

import me.piggypiglet.framework.Framework;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class HelpDocs {
    private HelpDocs() {
        Framework.builder()
                .main(this)
                .pckg("me.piggypiglet.helpdocs")
                .commandPrefixes("!")
                .build()
                .init();
    }

    public static void main(String[] args) {
        new HelpDocs();
    }
}
