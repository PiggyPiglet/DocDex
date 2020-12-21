package me.piggypiglet.docdex.console.registerables;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.console.ConsoleCommand;
import me.piggypiglet.docdex.scanning.framework.Scanner;
import me.piggypiglet.docdex.scanning.rules.Rules;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ConsoleCommandsRegisterable extends Registerable {
    private static final Named COMMANDS = Names.named("console commands");

    private final Scanner scanner;

    @Inject
    public ConsoleCommandsRegisterable(@NotNull final Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void execute(@NotNull final Injector injector) {
        addBinding(new TypeLiteral<Set<ConsoleCommand>>() {}, COMMANDS,
                new HashSet<>(scanner.getClasses(Rules.builder().typeExtends(ConsoleCommand.class).disallowMutableClasses().build())
                        .map(injector::getInstance)
                        .map(ConsoleCommand.class::cast)
                        .collect(Collectors.toSet())));
    }
}
