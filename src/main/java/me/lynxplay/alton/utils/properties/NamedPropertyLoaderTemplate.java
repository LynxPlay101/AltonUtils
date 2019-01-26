package me.lynxplay.alton.utils.properties;

import lombok.Getter;
import me.lynxplay.alton.utils.properties.adapter.PropertyTypeAdapter;

import java.util.Arrays;

/**
 * A simple template instance that holds pre-registered adapter instances
 */
public class NamedPropertyLoaderTemplate {

    @Getter
    private PropertyTypeAdapter[] adapters;

    /**
     * Creates a new loader template instance with the provided adapters
     *
     * @param adapters the adapters
     */
    public NamedPropertyLoaderTemplate(PropertyTypeAdapter... adapters) {
        this.adapters = adapters;
    }

    /**
     * Adds a new adapter to the template
     *
     * @param adapter the adapter instance
     * @param <T> the generic type of the java class the adapter can convert to
     * @return the instance of the template loader this addition was executed on
     */
    public <T> NamedPropertyLoaderTemplate add(PropertyTypeAdapter<T> adapter) {
        this.adapters = Arrays.copyOf(this.adapters, this.adapters.length + 1);
        this.adapters[this.adapters.length - 1] = adapter;
        return this;
    }
}
