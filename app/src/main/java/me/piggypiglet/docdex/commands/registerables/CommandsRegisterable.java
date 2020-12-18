package me.piggypiglet.docdex.commands.registerables;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.commands.Command;
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
public final class CommandsRegisterable extends Registerable {
    private static final Named COMMANDS = Names.named("commands");

    private final Scanner scanner;

    @Inject
    public CommandsRegisterable(@NotNull final Scanner scanner) {
        this.scanner = scanner;
    }

    @SuppressWarnings({"unchecked", "Convert2Diamond"})
    @Override
    protected void execute(@NotNull final Injector injector) {
        addBinding(new TypeLiteral<Set<Command>>() {}, COMMANDS,
                (Set<Command>) new HashSet<>(scanner.getClasses(Rules.builder().typeExtends(Command.class).disallowMutableClasses().build())
                        .map(injector::getInstance)
                        .collect(Collectors.toSet())));
    }
}
