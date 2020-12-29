package me.piggypiglet.docdex.db.adapters;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.piggypiglet.docdex.db.adapters.framework.DatabaseObjectAdapter;
import me.piggypiglet.docdex.db.adapters.framework.ModificationRequest;
import me.piggypiglet.docdex.db.orm.TableManager;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DatabaseObjectAdapters {
    private final Map<Class<?>, DatabaseObjectAdapter<?>> adapters;
    private final TableManager tableManager;

    @Inject
    public DatabaseObjectAdapters(@NotNull @Named("adapters") final Map<Class<?>, DatabaseObjectAdapter<?>> adapters,
                                  @NotNull final TableManager tableManager) {
        this.adapters = adapters;
        this.tableManager = tableManager;
    }

    @SuppressWarnings("unchecked")
    public <T> void save(@NotNull final T object) {
        final ModificationRequest modificationRequest = ((DatabaseObjectAdapter<T>) adapters.get(object.getClass())).applyToRaw(object);

        modificationRequest.getModified().forEach(tableManager::save);
        modificationRequest.getDeleted().forEach(tableManager::delete);
    }
}
