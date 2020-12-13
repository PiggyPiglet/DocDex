package me.piggypiglet.docdex.documentation.index.data.population.registerables;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.documentation.index.data.population.IndexPopulator;
import me.piggypiglet.docdex.scanning.framework.Scanner;
import me.piggypiglet.docdex.scanning.rules.Rules;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class IndexPopulatorRegisterable extends Registerable {
    private static final Named POPULATORS = Names.named("populators");

    private final Scanner scanner;

    @Inject
    public IndexPopulatorRegisterable(@NotNull final Scanner scanner) {
        this.scanner = scanner;
    }

    @SuppressWarnings({"Convert2Diamond", "unchecked"})
    @Override
    protected void execute(final @NotNull Injector injector) {
        addBinding(new TypeLiteral<Set<IndexPopulator>>() {}, POPULATORS,
                (Set<IndexPopulator>) scanner.getClasses(Rules.builder().typeExtends(IndexPopulator.class).disallowMutableClasses().build())
                        .map(injector::getInstance)
                        .collect(Collectors.toSet()));
    }
}
