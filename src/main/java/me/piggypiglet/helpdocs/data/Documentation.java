package me.piggypiglet.helpdocs.data;

import me.piggypiglet.framework.managers.implementations.SearchableManager;
import me.piggypiglet.framework.managers.objects.KeyTypeInfo;
import me.piggypiglet.framework.utils.annotations.reflection.Disabled;

import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Disabled
public final class Documentation extends SearchableManager<Type> {
    private final String version;
    private final Map<String, Type> types;

    public Documentation(String version, Map<String, Type> types) {
        this.version = version;
        this.types = types;
    }

    public String getVersion() {
        return version;
    }

    @Override
    protected KeyTypeInfo configure(KeyTypeInfo.Builder builder) {
        return builder
                .key(String.class)
                    .map(types)
                .build();
    }

    @Override
    protected void insert(Type item) {
        types.put(item.getName(), item);
    }

    @Override
    protected void delete(Type item) {
        types.remove(item.getName());
    }
}
