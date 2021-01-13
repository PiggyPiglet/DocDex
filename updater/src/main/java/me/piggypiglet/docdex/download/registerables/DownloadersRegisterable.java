package me.piggypiglet.docdex.download.registerables;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.download.framework.JavadocDownloader;
import me.piggypiglet.docdex.scanning.framework.Scanner;
import me.piggypiglet.docdex.scanning.rules.Rules;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DownloadersRegisterable extends Registerable {
    private static final Named DOWNLOADERS = Names.named("downloaders");
    private static final Rules RULES = Rules.builder()
            .typeExtends(JavadocDownloader.class)
            .disallowMutableClasses()
            .build();

    private final Scanner scanner;

    @Inject
    public DownloadersRegisterable(@NotNull final Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void execute(final @NotNull Injector injector) {
        addBinding(new TypeLiteral<Map<Class<?>, JavadocDownloader<?>>>() {}, DOWNLOADERS,
                scanner.getClasses(RULES)
                        .map(injector::getInstance)
                        .map(downloader -> (JavadocDownloader<?>) downloader)
                        .collect(Collectors.toMap(downloader -> TypeLiteral.get(
                                ((ParameterizedType) downloader.getClass().getGenericSuperclass()).getActualTypeArguments()[0]
                        ).getRawType(), downloader -> downloader)));
    }
}
