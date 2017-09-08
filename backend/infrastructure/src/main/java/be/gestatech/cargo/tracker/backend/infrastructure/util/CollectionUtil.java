package be.gestatech.cargo.tracker.backend.infrastructure.util;

import java.util.*;


public final class CollectionUtil {

    private static final String DEFAULT_NOT_EMPTY_ARRAY_EX_MESSAGE = "The validated array is empty";

    private static final String DEFAULT_NOT_EMPTY_COLLECTION_EX_MESSAGE = "The validated collection is empty";

    private static final String DEFAULT_NOT_EMPTY_MAP_EX_MESSAGE = "The validated map is empty";

    private static final String DEFAULT_NO_NULL_ELEMENTS_COLLECTION_EX_MESSAGE = "The validated collection contains null element at index: %d";

    private CollectionUtil() {
        throw new AssertionError("No CollectionUtil instances allowed!");
    }

    // emptyList
    //---------------------------------------------------------------------------------

    public static <T> List<T> emptyList() {
        return Collections.emptyList();
    }

    // notEmpty array
    //---------------------------------------------------------------------------------

    public static <T> T[] notEmpty(final T[] array, final String message, final Object... values) {
        if (ObjectUtil.isNull(array)) {
            throw new NullPointerException(String.format(message, values));
        }
        if (ObjectUtil.equals(array.length, 0)) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return array;
    }

    public static <T> T[] notEmpty(final T[] array) {
        return notEmpty(array, DEFAULT_NOT_EMPTY_ARRAY_EX_MESSAGE);
    }

    // notEmpty collection
    //---------------------------------------------------------------------------------

    public static <T extends Collection<?>> T notEmpty(final T collection, final String message, final Object... values) {
        if (ObjectUtil.isNull(collection)) {
            throw new NullPointerException(String.format(message, values));
        }
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return collection;
    }

    public static <T extends Collection<?>> T notEmpty(final T collection) {
        return notEmpty(collection, DEFAULT_NOT_EMPTY_COLLECTION_EX_MESSAGE);
    }

    // notEmpty map
    //---------------------------------------------------------------------------------

    public static <T extends Map<?, ?>> T notEmpty(final T map, final String message, final Object... values) {
        if (ObjectUtil.isNull(map)) {
            throw new NullPointerException(String.format(message, values));
        }
        if (map.isEmpty()) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return map;
    }

    public static <T extends Map<?, ?>> T notEmpty(final T map) {
        return notEmpty(map, DEFAULT_NOT_EMPTY_MAP_EX_MESSAGE);
    }

    // noNullElements iterable
    //---------------------------------------------------------------------------------

    public static <T extends Iterable<?>> T noNullElements(final T iterable, final String message, final Object... values) {
        ObjectUtil.nonNull(iterable);
        int i = 0;
        for (final Iterator<?> it = iterable.iterator(); it.hasNext(); i++) {
            if (ObjectUtil.isNull(it.next())) {
                final Object[] newValues = ArrayUtil.addAll(values, Integer.valueOf(i));
                throw new IllegalArgumentException(String.format(message, newValues));
            }
        }
        return iterable;
    }

    public static <T extends Iterable<?>> T noNullElements(final T iterable) {
        return noNullElements(iterable, DEFAULT_NO_NULL_ELEMENTS_COLLECTION_EX_MESSAGE);
    }
}
