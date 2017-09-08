package be.gestatech.cargo.tracker.backend.infrastructure.util;

import java.lang.reflect.Array;

public final class ArrayUtil {

    private static final String DEFAULT_NO_NULL_ELEMENTS_ARRAY_EX_MESSAGE = "The validated array contains null element at index: %d";

    private ArrayUtil() {
        throw new AssertionError("No ArrayUtil instances allowed!");
    }

    // Clone
    //-----------------------------------------------------------------------

    public static <T> T[] clone(final T[] array) {
        T[] response = null;
        if (ObjectUtil.nonNull(array)) {
            response = array.clone();
        }
        return response;
    }

    // add
    //---------------------------------------------------------------------------------

    public static <T> T[] add(final T[] array, final T element) {
        Class<?> type;
        if (ObjectUtil.nonNull(array)) {
            type = array.getClass().getComponentType();
        } else if (ObjectUtil.nonNull(element)) {
            type = element.getClass();
        } else {
            throw new IllegalArgumentException("Arguments cannot both be null");
        }
        @SuppressWarnings("unchecked") // type must be T
        final T[] newArray = (T[]) copyArrayGrow(array, type);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    // addAll
    //---------------------------------------------------------------------------------

    public static <T> T[] addAll(final T[] origine, final T... destination) {
        if (ObjectUtil.isNull(origine)) {
            return clone(destination);
        } else if (ObjectUtil.isNull(destination)) {
            return clone(origine);
        }
        final Class<?> origineType = origine.getClass().getComponentType();
        @SuppressWarnings("unchecked") // OK, because array is of type T
        final T[] joinedArray = (T[]) Array.newInstance(origineType, origine.length + destination.length);
        System.arraycopy(origine, 0, joinedArray, 0, origine.length);
        try {
            System.arraycopy(destination, 0, joinedArray, origine.length, destination.length);
        } catch (final ArrayStoreException ase) {
            final Class<?> destinationType = destination.getClass().getComponentType();
            if (!origineType.isAssignableFrom(destinationType)) {
                throw new IllegalArgumentException(String.format("Cannot store [%s] in an array of [%s]", origineType.getName(), destinationType.getName()), ase);
            }
            throw ase;
        }
        return joinedArray;
    }

    // noNullElements array
    //---------------------------------------------------------------------------------

    public static <T> T[] noNullElements(final T[] array, final String message, final Object... values) {
        ObjectUtil.nonNull(array);
        for (int i = 0; i < array.length; i++) {
            if (ObjectUtil.isNull(array[i])) {
                final Object[] values2 = ArrayUtil.add(values, Integer.valueOf(i));
                throw new IllegalArgumentException(String.format(message, values2));
            }
        }
        return array;
    }

    public static <T> T[] noNullElements(final T[] array) {
        return noNullElements(array, DEFAULT_NO_NULL_ELEMENTS_ARRAY_EX_MESSAGE);
    }

    // P R I V A T E    S E C T I O N
    //---------------------------------------------------------------------------------

    private static Object copyArrayGrow(final Object array, final Class<?> newArrayComponentType) {
        if (ObjectUtil.nonNull(array)) {
            final int arrayLength = Array.getLength(array);
            final Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
            System.arraycopy(array, 0, newArray, 0, arrayLength);
            return newArray;
        }
        return Array.newInstance(newArrayComponentType, 1);
    }
}
