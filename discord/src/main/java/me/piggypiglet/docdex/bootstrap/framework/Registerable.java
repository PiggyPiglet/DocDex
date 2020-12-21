package me.piggypiglet.docdex.bootstrap.framework;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import me.piggypiglet.docdex.guice.modules.DynamicModule;
import me.piggypiglet.docdex.guice.objects.Binding;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class Registerable {
    private final Set<Binding<?>> bindings = new HashSet<>();
    private final Set<Class<?>> staticInjections = new HashSet<>();

    public void execute() {}

    public void execute(@NotNull final Injector injector) {}

    public final void run(@NotNull final Injector injector) {
        execute();
        execute(injector);
    }

    protected <T> void addBinding(@NotNull final Class<? super T> type, @NotNull final T instance) {
        addBinding(Key.get(type), instance);
    }

    protected <T> void addBinding(@NotNull final TypeLiteral<? super T> type, @NotNull final T instance) {
        addBinding(Key.get(type), instance);
    }

    protected <T> void addBinding(@NotNull final Class<? super T> type, @NotNull final Annotation annotation,
                                  @NotNull final T instance) {
        addBinding(Key.get(type, annotation), instance);
    }

    protected <T> void addBinding(@NotNull final Class<? super T> type, @NotNull final Class<? extends Annotation> annotation,
                                  @NotNull final T instance) {
        addBinding(Key.get(type, annotation), instance);
    }

    protected <T> void addBinding(@NotNull final TypeLiteral<? super T> type, @NotNull final Annotation annotation,
                                  @NotNull final T instance) {
        addBinding(Key.get(type, annotation), instance);
    }

    protected <T> void addBinding(@NotNull final TypeLiteral<? super T> type, @NotNull final Class<? extends Annotation> annotation,
                                  @NotNull final T instance) {
        addBinding(Key.get(type, annotation), instance);
    }

    protected <T> void addBinding(@NotNull final Key<? super T> key, @NotNull final T instance) {
        bindings.add(new Binding<>(key, instance));
    }

    protected void requestStaticInjections(@NotNull final Class<?>@NotNull... classes) {
        staticInjections.addAll(Arrays.asList(classes));
    }

    @NotNull
    public Optional<DynamicModule> createModule() {
        if (bindings.isEmpty() && staticInjections.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new DynamicModule(bindings, staticInjections.toArray(new Class<?>[]{})));
    }
}
