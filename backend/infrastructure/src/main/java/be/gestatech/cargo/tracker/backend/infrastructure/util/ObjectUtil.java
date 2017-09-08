package be.gestatech.cargo.tracker.backend.infrastructure.util;

import java.util.Arrays;
import java.util.Objects;

public final class ObjectUtil {

    private static final String DEFAULT_IS_NULL_EX_MESSAGE = "The validated object is null";

    private static final String DEFAULT_IS_NOT_NULL_EX_MESSAGE = "The validated object is not null";

    private static final String DEFAULT_IS_TRUE_EX_MESSAGE = "The validated expression is false";

    private static final String DEFAULT_IS_FALSE_EX_MESSAGE = "The validated expression is true";

    private ObjectUtil() {
        throw new AssertionError("No ObjectUtil instances allowed!");
    }

    // equals
    //---------------------------------------------------------------------------------

    public static boolean equals(Object var0, Object var1) {
        return Objects.equals(var0, var1) || nonNull(var0) && var0.equals(var1);
    }

    // hashCode
    //---------------------------------------------------------------------------------

    public static int hashCode(Object var0) {
        return nonNull(var0) ? var0.hashCode() : 0;
    }

    // hash
    //---------------------------------------------------------------------------------

    public static int hash(Object... var0) {
        return Arrays.hashCode(var0);
    }

    // nonNull
    //---------------------------------------------------------------------------------

    public static <T> boolean nonNull(final T object) {
        boolean response = false;
        if (Objects.nonNull(object)) {
            response = true;
        }
        return response;
    }

    // requireNonNull
    //---------------------------------------------------------------------------------

    public static <T> T requireNonNull(final T object) {
        return requireNonNull(object, DEFAULT_IS_NULL_EX_MESSAGE);
    }

    public static <T> T requireNonNull(final T object, final String message, final Object... values) {
        if (Objects.isNull(object)) {
            throw new NullPointerException(String.format(message, values));
        }
        return object;
    }

    // isNull
    //---------------------------------------------------------------------------------

    public static <T> boolean isNull(final T object) {
        boolean response = true;
        if (Objects.nonNull(object)) {
            response = false;
        }
        return response;
    }

    // requireNull
    //---------------------------------------------------------------------------------

    public static <T> T requireNull(final T object) {
        return requireNull(object, DEFAULT_IS_NOT_NULL_EX_MESSAGE);
    }

    public static <T> T requireNull(final T object, final String message, final Object... values) {
        if (Objects.nonNull(object)) {
            throw new NullPointerException(String.format(message, values));
        }
        return object;
    }

    // nullSafe
    //---------------------------------------------------------------------------------

    public static <T> T nullSafe(T actual, T safe) {
        return Objects.isNull(actual) ? safe : actual;
    }

    // isTrue
    //---------------------------------------------------------------------------------

    public static boolean isTrue(final boolean expression) {
        return Objects.equals(expression, true);
    }

    // requireTrue
    //---------------------------------------------------------------------------------

    public static void requireTrue(final boolean expression, final String message, final long value) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, Long.valueOf(value)));
        }
    }

    public static void requireTrue(final boolean expression, final String message, final double value) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, Double.valueOf(value)));
        }
    }

    public static void requireTrue(final boolean expression, final String message, final Object... values) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void requireTrue(final boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException(DEFAULT_IS_TRUE_EX_MESSAGE);
        }
    }

    // isFalse
    //---------------------------------------------------------------------------------

    public static boolean isFalse(final boolean expression) {
        return Objects.equals(expression, false);
    }

    // requireFalse
    //---------------------------------------------------------------------------------

    public static void requireFalse(final boolean expression, final String message, final long value) {
        if (expression) {
            throw new IllegalArgumentException(String.format(message, Long.valueOf(value)));
        }
    }

    public static void requireFalse(final boolean expression, final String message, final double value) {
        if (expression) {
            throw new IllegalArgumentException(String.format(message, Double.valueOf(value)));
        }
    }

    public static void requireFalse(final boolean expression, final String message, final Object... values) {
        if (expression) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void requireFalse(final boolean expression) {
        if (expression) {
            throw new IllegalArgumentException(DEFAULT_IS_FALSE_EX_MESSAGE);
        }
    }

}
