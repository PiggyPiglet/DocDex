package me.piggypiglet.docdex.file.registerables;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.file.annotations.File;
import me.piggypiglet.docdex.scanning.framework.Scanner;
import me.piggypiglet.docdex.scanning.rules.Rules;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class FileObjectsRegisterable extends Registerable {
    private static final Named FILES = Names.named("files");

    private final Scanner scanner;

    @Inject
    public FileObjectsRegisterable(@NotNull final Scanner scanner) {
        this.scanner = scanner;
    }

    @SuppressWarnings("Convert2Diamond")
    @Override
    protected void execute(@NotNull final Injector injector) {
        final Map<Class<?>, Object> fileObjects = scanner.getClasses(Rules.builder()
                .hasAnnotation(File.class)
                .build()).collect(Collectors.toMap(clazz -> clazz, injector::getInstance));

        addBinding(new TypeLiteral<Set<Class<?>>>() {}, FILES, fileObjects.keySet());
        addBinding(new TypeLiteral<Map<Class<?>, Object>>() {}, FILES, fileObjects);
    }
}
