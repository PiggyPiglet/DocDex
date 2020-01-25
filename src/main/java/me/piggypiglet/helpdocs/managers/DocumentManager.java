package me.piggypiglet.helpdocs.managers;

import com.google.inject.Singleton;
import me.piggypiglet.framework.managers.Manager;
import me.piggypiglet.framework.managers.objects.KeyTypeInfo;
import me.piggypiglet.helpdocs.data.Documentation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class DocumentManager extends Manager<Documentation> {
    private final Map<String, Documentation> documents = new HashMap<>();

    @Override
    protected KeyTypeInfo configure(KeyTypeInfo.Builder builder) {
        return builder
                .key(String.class)
                    .map(documents)
                .build();
    }

    @Override
    protected void insert(Documentation item) {

    }

    @Override
    protected void delete(Documentation item) {

    }

    @Override
    protected Collection<Documentation> retrieveAll() {
        return null;
    }
}
