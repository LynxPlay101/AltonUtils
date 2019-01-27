package me.lynxplay.alton.utils.properties.adapter;

import java.util.function.Function;

public class SimplePropertyTypeAdapter<T> implements PropertyTypeAdapter<T> {

    private final Class<T> type;
    private final Function<String, T> converter;

    public SimplePropertyTypeAdapter(Class<T> type, Function<String, T> converter) {
        this.type = type;
        this.converter = converter;
    }

    /**
     * Converts a given input string into the passed target type
     *
     * @param input the input string to convert
     * @return the object
     */
    @Override
    public T convert(String input) {
        return this.converter.apply(input);
    }

    /**
     * Returns the type this adapter can convert
     *
     * @return the type
     */
    @Override
    public Class<T> getConvertingType() {
        return this.type;
    }
}
