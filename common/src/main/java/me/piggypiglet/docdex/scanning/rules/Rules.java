package me.piggypiglet.docdex.scanning.rules;

import me.piggypiglet.docdex.scanning.annotations.Hidden;
import me.piggypiglet.docdex.scanning.rules.annotation.AnnotationUtils;
import me.piggypiglet.docdex.scanning.rules.annotation.AnnotationWrapper;
import me.piggypiglet.docdex.scanning.rules.element.ElementWrapper;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class Rules {
    private static final Rules EMPTY = new Rules(Collections.emptyList());

    private final List<Predicate<ElementWrapper>> rules;

    private Rules(@NotNull final List<Predicate<ElementWrapper>> rules) {
        this.rules = rules;
    }

    @NotNull
    public List<Predicate<ElementWrapper>> getRules() {
        return rules;
    }

    @NotNull
    public static Rules empty() {
        return EMPTY;
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<Predicate<ElementWrapper>> rules = new ArrayList<>();

        private Builder() {
            addAnnotated(element -> !element.isAnnotationPresent(Hidden.class));
        }

        @NotNull
        public Builder add(@NotNull final Predicate<ElementWrapper> rule) {
            rules.add(rule);
            return this;
        }

        @NotNull
        public Builder addType(@NotNull final Predicate<Class<?>> typeRule) {
            rules.add(element -> typeRule.test(element.getType()));
            return this;
        }

        @NotNull
        public Builder addAnnotated(@NotNull final Predicate<AnnotatedElement> annotatedRule) {
            rules.add(element -> annotatedRule.test(element.getElement()));
            return this;
        }

        @NotNull
        public <T extends Annotation> Builder addAnnotated(@NotNull final Class<T> annotation, @NotNull final Predicate<T> annotationRule) {
            rules.add(element -> annotationRule.test(element.getElement().getAnnotation(annotation)));
            return this;
        }

        @NotNull
        public Builder typeEquals(@NotNull final Class<?> type) {
            return addType(element -> element == type);
        }

        @NotNull
        public Builder typeExtends(@NotNull final Class<?> type) {
            return addType(type::isAssignableFrom);
        }

        @NotNull
        public Builder typeSupers(@NotNull final Class<?> type) {
            return addType(element -> element.isAssignableFrom(type));
        }

        @NotNull
        public Builder disallowImmutableClasses() {
            return addType(element -> !Modifier.isFinal(element.getModifiers()));
        }

        @NotNull
        public Builder disallowMutableClasses() {
            return addType(element -> !Modifier.isAbstract(element.getModifiers()) && !element.isInterface());
        }

        @NotNull
        public Builder hasAnnotation(@NotNull final Class<? extends Annotation> annotation) {
            return hasAnnotation(new AnnotationWrapper(annotation));
        }

        @NotNull
        public Builder hasAnnotation(@NotNull final Annotation annotation) {
            return hasAnnotation(new AnnotationWrapper(annotation));
        }

        @NotNull
        private Builder hasAnnotation(@NotNull final AnnotationWrapper annotationWrapper) {
            return addAnnotated(element -> AnnotationUtils.isAnnotationPresent(element, annotationWrapper));
        }

        @NotNull
        public Builder allowHidden() {
            rules.remove(0);
            return this;
        }

        @NotNull
        public Rules build() {
            return new Rules(rules);
        }
    }
}
