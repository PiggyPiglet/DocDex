package me.piggypiglet.docdex.documentation.routes.serialization.registerables;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.documentation.routes.serialization.JsonSerializer;
import me.piggypiglet.docdex.scanning.framework.Scanner;
import me.piggypiglet.docdex.scanning.rules.Rules;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class JsonSerializerRegisterable extends Registerable {
    private static final Named JSON_SERIALIZERS = Names.named("json serializers");

    private final Scanner scanner;

    @Inject
    public JsonSerializerRegisterable(@NotNull final Scanner scanner) {
        this.scanner = scanner;
    }

    @SuppressWarnings({"Convert2Diamond", "unchecked"})
    @Override
    protected void execute(final @NotNull Injector injector) {
        addBinding(new TypeLiteral<Set<JsonSerializer>>() {}, JSON_SERIALIZERS,
                (Set<JsonSerializer>) scanner.getClasses(Rules.builder().typeExtends(JsonSerializer.class).disallowMutableClasses().build())
                        .map(injector::getInstance)
                        .collect(Collectors.toSet()));
    }
}
