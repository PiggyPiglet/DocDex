package me.piggypiglet.docdex.documentation.index.storage.registerables;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.documentation.index.storage.IndexStorage;
import me.piggypiglet.docdex.scanning.framework.Scanner;
import me.piggypiglet.docdex.scanning.rules.Rules;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class IndexStorageRegisterable extends Registerable {
    private static final Named STORAGE = Names.named("storage");

    private final Scanner scanner;

    @Inject
    public IndexStorageRegisterable(@NotNull final Scanner scanner) {
        this.scanner = scanner;
    }

    @SuppressWarnings({"Convert2Diamond"})
    @Override
    public void execute(@NotNull final Injector injector) {
        addBinding(new TypeLiteral<Set<IndexStorage>>() {}, STORAGE,
                scanner.getClasses(Rules.builder().typeExtends(IndexStorage.class).disallowMutableClasses().build())
                        .map(injector::getInstance)
                        .map(IndexStorage.class::cast)
                        .collect(Collectors.toSet()));
    }
}
