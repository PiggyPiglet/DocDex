package me.piggypiglet.docdex.bot.commands.registerables;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.bot.commands.framework.BotCommand;
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
public final class BotCommandsRegisterable extends Registerable {
    private static final Named COMMANDS = Names.named("jda commands");

    private final Scanner scanner;

    @Inject
    public BotCommandsRegisterable(@NotNull final Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void execute(@NotNull final Injector injector) {
        addBinding(new TypeLiteral<Set<BotCommand>>() {}, COMMANDS,
                new HashSet<>(scanner.getClasses(Rules.builder().typeExtends(BotCommand.class).disallowMutableClasses().build())
                        .map(injector::getInstance)
                        .map(BotCommand.class::cast)
                        .collect(Collectors.toSet())));
    }
}
