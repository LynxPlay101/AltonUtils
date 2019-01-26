package me.lynxplay.alton.utils.properties.adapter;

/**
 * This interface defines an adapter for a given value
 */
public interface PropertyTypeAdapter<T> {

    /**
     * Converts a given input string into the passed target type
     *
     * @param input the input string to convert
     * @return the object
     */
    T convert(String input);

    /**
     * Returns the type this adapter can convert
     *
     * @return the type
     */
    Class<T> getConvertingType();
}
