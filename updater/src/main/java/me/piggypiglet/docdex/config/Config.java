package me.piggypiglet.docdex.config;

import com.google.inject.Singleton;
import me.piggypiglet.docdex.file.annotations.File;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@File(
        internalPath = "/config.json",
        externalPath = "config.json"
) @Singleton
public final class Config {

}
