package me.piggypiglet.docdex.db.orm.structure.registerables;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.db.orm.structure.generation.StructureGenerator;
import me.piggypiglet.docdex.scanning.framework.Scanner;
import me.piggypiglet.docdex.scanning.rules.Rules;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class StructureAdaptersRegisterable extends Registerable {
    private static final Named STRUCTURE_ADAPTERS = Names.named("structure adapters");

    private final Scanner scanner;

    @Inject
    public StructureAdaptersRegisterable(@NotNull final Scanner scanner) {
        this.scanner = scanner;
    }

    @SuppressWarnings("Convert2Diamond")
    @Override
    public void execute(final @NotNull Injector injector) {
        addBinding(new TypeLiteral<Set<StructureGenerator>>() {}, STRUCTURE_ADAPTERS,
                scanner.getClasses(Rules.builder().typeExtends(StructureGenerator.class).disallowMutableClasses().build())
                        .map(injector::getInstance)
                        .map(StructureGenerator.class::cast)
                        .collect(Collectors.toSet()));
    }
}
