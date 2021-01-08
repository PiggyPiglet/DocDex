package me.piggypiglet.docdex.db.orm.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    @NotNull
    String value();
}
