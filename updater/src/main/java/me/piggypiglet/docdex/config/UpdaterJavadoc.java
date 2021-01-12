package me.piggypiglet.docdex.config;

import com.google.gson.annotations.JsonAdapter;
import me.piggypiglet.docdex.config.deserialization.UpdateStrategyDeserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class UpdaterJavadoc {
    private Set<String> names;
    private String link;
    private String actualLink;
    @JsonAdapter(UpdateStrategyDeserializer.class) private Object strategy;

    @NotNull
    public Set<String> getNames() {
        return names;
    }

    @NotNull
    public String getLink() {
        return link;
    }

    @NotNull
    public String getActualLink() {
        return actualLink;
    }

    @Nullable
    public Object getStrategy() {
        return strategy;
    }
}
