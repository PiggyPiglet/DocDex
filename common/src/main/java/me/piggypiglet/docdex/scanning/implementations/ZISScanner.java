package me.piggypiglet.docdex.scanning.implementations;

import me.piggypiglet.docdex.scanning.annotations.Hidden;
import me.piggypiglet.docdex.scanning.exceptions.ScanningException;
import me.piggypiglet.docdex.scanning.framework.Scanner;
import me.piggypiglet.docdex.scanning.rules.RuleUtils;
import me.piggypiglet.docdex.scanning.rules.Rules;
import me.piggypiglet.docdex.scanning.rules.element.ElementWrapper;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ZISScanner implements Scanner {
    private final Set<Class<?>> classes;

    private ZISScanner(@NotNull final Set<Class<?>> classes) {
        this.classes = classes;
    }

    @NotNull
    public static ZISScanner create(@NotNull final Class<?> main, @NotNull final String pckg) {
        final ClassLoader loader = main.getClassLoader();
        final File src = new File(main.getProtectionDomain().getCodeSource().getLocation().getPath().split("!")[0].replace("file:/", ""));

        return create(src, loader, pckg);
    }

    @NotNull
    public static ZISScanner create(@NotNull final File jar, @NotNull final ClassLoader loader,
                                    @NotNull final String pckg) {
        final Set<Class<?>> classes = new HashSet<>();

        try (final ZipInputStream zip = new ZipInputStream(new FileInputStream(jar))) {
            ZipEntry entry;

            while ((entry = zip.getNextEntry()) != null) {
                final String name = entry.getName();

                if (!name.endsWith(".class") || !name.startsWith(pckg)) {
                    continue;
                }

                final Class<?> clazz = loadClass(loader, name.replace('/', '.').replace(".class", ""));

                if (!clazz.isAnnotationPresent(Hidden.class)) {
                    classes.add(clazz);
                }
            }
        } catch (final Exception exception) {
            throw new ScanningException(exception);
        }

        return new ZISScanner(classes);
    }

    @NotNull
    private static Class<?> loadClass(@NotNull final ClassLoader loader, @NotNull final String name) {
        try {
            return loader.loadClass(name);
        } catch (final Exception exception) {
            throw new ScanningException(exception);
        }
    }

    @NotNull
    @Override
    public Stream<Class<?>> getClasses(@NotNull final Rules rules) {
        return classes.stream()
                .filter(clazz -> RuleUtils.match(new ElementWrapper(clazz, clazz), rules));
    }

    @NotNull
    @Override
    public Stream<Parameter> getParametersInConstructors(@NotNull final Rules rules) {
        return classes.stream()
                .map(Class::getDeclaredConstructors)
                .flatMap(Arrays::stream)
                .map(Constructor::getParameters)
                .flatMap(Arrays::stream)
                .filter(parameter -> RuleUtils.match(new ElementWrapper(parameter.getType(), parameter), rules));
    }

    @NotNull
    @Override
    public Stream<Field> getFields(@NotNull final Rules rules) {
        return classes.stream()
                .map(Class::getDeclaredFields)
                .flatMap(Arrays::stream)
                .filter(field -> RuleUtils.match(new ElementWrapper(field.getType(), field), rules));
    }
}
