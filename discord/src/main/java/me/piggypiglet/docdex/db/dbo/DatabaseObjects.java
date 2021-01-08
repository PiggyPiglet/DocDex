package me.piggypiglet.docdex.db.dbo;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.db.dbo.framework.DatabaseObjectCreator;
import me.piggypiglet.docdex.db.dbo.framework.adapter.DatabaseObjectAdapter;
import me.piggypiglet.docdex.db.dbo.framework.adapter.ModificationRequest;
import me.piggypiglet.docdex.db.orm.TableManager;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@SuppressWarnings("unchecked")
public final class DatabaseObjects {
    private final Map<Class<?>, DatabaseObjectCreator<?>> creators;
    private final Map<Class<?>, DatabaseObjectAdapter<?>> adapters;
    private final TableManager tableManager;

    @Inject
    public DatabaseObjects(@NotNull @Named("creators") final Map<Class<?>, DatabaseObjectCreator<?>> creators,
                           @NotNull @Named("adapters") final Map<Class<?>, DatabaseObjectAdapter<?>> adapters,
                           @NotNull final TableManager tableManager) {
        this.creators = creators;
        this.adapters = adapters;
        this.tableManager = tableManager;
    }

    @NotNull
    public <T> T createInstance(@NotNull final Class<T> clazz) {
        return ((DatabaseObjectCreator<T>) creators.get(clazz)).createInstance();
    }

    public <T> void save(@NotNull final T object) {
        final ModificationRequest modificationRequest = ((DatabaseObjectAdapter<T>) adapters.get(object.getClass())).applyToRaw(object);

        modificationRequest.getModified().forEach(tableManager::save);
        modificationRequest.getDeleted().forEach(tableManager::delete);
    }
}
