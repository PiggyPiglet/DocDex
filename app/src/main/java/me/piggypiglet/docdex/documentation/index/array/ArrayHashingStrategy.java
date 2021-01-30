package me.piggypiglet.docdex.documentation.index.array;

import gnu.trove.strategy.HashingStrategy;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ArrayHashingStrategy<E> implements HashingStrategy<E> {
    private static final ArrayHashingStrategy<?> INSTANCE = new ArrayHashingStrategy<>();

    private ArrayHashingStrategy() {}

    @NotNull
    @SuppressWarnings("unchecked")
    public static <E> HashingStrategy<E> get() {
        return (HashingStrategy<E>) INSTANCE;
    }

    @NotNull
    public static <E> HashingStrategy<E> create() {
        return new ArrayHashingStrategy<>();
    }

    @Override
    public int computeHashCode(final E object) {
        final Class<?> clazz = object.getClass();

        if (!clazz.isArray()) {
            return Objects.hashCode(object);
        }

        switch (object.getClass().getSimpleName()) {
            case "long[]":
                return Arrays.hashCode((long[]) object);
            case "short[]":
                return Arrays.hashCode((short[]) object);
            case "char[]":
                return Arrays.hashCode((char[]) object);
            case "byte[]":
                return Arrays.hashCode((byte[]) object);
            case "boolean[]":
                return Arrays.hashCode((boolean[]) object);
            case "float[]":
                return Arrays.hashCode((float[]) object);
            case "double[]":
                return Arrays.hashCode((double[]) object);
            default:
                return Arrays.hashCode((Object[]) object);
        }
    }

    @Override
    public boolean equals(final E o1, final E o2) {
        final Class<?> class1 = o1.getClass();
        final Class<?> class2 = o2.getClass();

        if (!class1.isArray() || !class2.isArray()) {
            return Objects.equals(o1, o2);
        }

        final String simpleName1 = class1.getSimpleName();
        final String simpleName2 = class2.getSimpleName();

        if (!simpleName1.equals(simpleName2)) {
            return Arrays.equals((Object[]) o1, (Object[]) o2);
        }

        switch (simpleName1) {
            case "long[]":
                return Arrays.equals((long[]) o1, (long[]) o2);
            case "short[]":
                return Arrays.equals((short[]) o1, (short[]) o2);
            case "char[]":
                return Arrays.equals((char[]) o1, (char[]) o2);
            case "byte[]":
                return Arrays.equals((byte[]) o1, (byte[]) o2);
            case "boolean[]":
                return Arrays.equals((boolean[]) o1, (boolean[]) o2);
            case "float[]":
                return Arrays.equals((float[]) o1, (float[]) o2);
            case "double[]":
                return Arrays.equals((double[]) o1, (double[]) o2);
            default:
                return Arrays.equals((Object[]) o1, (Object[]) o2);
        }
    }
}
