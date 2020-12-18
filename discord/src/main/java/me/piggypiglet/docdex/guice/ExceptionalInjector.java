package me.piggypiglet.docdex.guice;

import com.google.inject.Module;
import com.google.inject.*;
import com.google.inject.spi.Element;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.TypeConverterBinding;
import me.piggypiglet.docdex.guice.exceptions.InjectionException;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ExceptionalInjector implements Injector {
    private final Injector composition;

    public ExceptionalInjector(@NotNull final Injector injector) {
        composition = injector;
    }

    @Override
    public void injectMembers(@NotNull final Object instance) {
        composition.injectMembers(instance);
    }

    @Override
    public <T> MembersInjector<T> getMembersInjector(@NotNull final TypeLiteral<T> typeLiteral) {
        return composition.getMembersInjector(typeLiteral);
    }

    @Override
    public <T> MembersInjector<T> getMembersInjector(@NotNull final Class<T> type) {
        return composition.getMembersInjector(type);
    }

    @Override
    public Map<Key<?>, Binding<?>> getBindings() {
        return composition.getBindings();
    }

    @Override
    public Map<Key<?>, Binding<?>> getAllBindings() {
        return composition.getAllBindings();
    }

    @Override
    public <T> Binding<T> getBinding(@NotNull final Key<T> key) {
        return composition.getBinding(key);
    }

    @Override
    public <T> Binding<T> getBinding(@NotNull final Class<T> type) {
        return composition.getBinding(type);
    }

    @Override
    public <T> Binding<T> getExistingBinding(@NotNull final Key<T> key) {
        return composition.getExistingBinding(key);
    }

    @Override
    public <T> List<Binding<T>> findBindingsByType(@NotNull final TypeLiteral<T> type) {
        return composition.findBindingsByType(type);
    }

    @Override
    public <T> Provider<T> getProvider(@NotNull final Key<T> key) {
        return composition.getProvider(key);
    }

    @Override
    public <T> Provider<T> getProvider(@NotNull final Class<T> type) {
        return composition.getProvider(type);
    }

    @Override
    public <T> T getInstance(@NotNull final Key<T> key) {
        try {
            return composition.getInstance(key);
        } catch (final Exception exception) {
            throw new InjectionException(exception);
        }
    }

    @Override
    public <T> T getInstance(@NotNull final Class<T> type) {
        try {
            return composition.getInstance(type);
        } catch (final Exception exception) {
            throw new InjectionException(exception);
        }
    }

    @Override
    public Injector getParent() {
        return composition.getParent();
    }

    @Override
    public Injector createChildInjector(@NotNull final Iterable<? extends Module> modules) {
        return new ExceptionalInjector(composition.createChildInjector(modules));
    }

    @Override
    public Injector createChildInjector(@NotNull final Module @NotNull ... modules) {
        return new ExceptionalInjector(composition.createChildInjector(modules));
    }

    @Override
    public Map<Class<? extends Annotation>, Scope> getScopeBindings() {
        return composition.getScopeBindings();
    }

    @Override
    public Set<TypeConverterBinding> getTypeConverterBindings() {
        return composition.getTypeConverterBindings();
    }

    @Override
    public List<Element> getElements() {
        return composition.getElements();
    }

    @Override
    public Map<TypeLiteral<?>, List<InjectionPoint>> getAllMembersInjectorInjectionPoints() {
        return composition.getAllMembersInjectorInjectionPoints();
    }
}
