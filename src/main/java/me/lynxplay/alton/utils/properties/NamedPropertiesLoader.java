package me.lynxplay.alton.utils.properties;

import com.google.common.base.Defaults;
import com.google.common.primitives.Primitives;
import me.lynxplay.alton.utils.properties.adapter.PropertyTypeAdapter;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * This class is used to load values from a property instance into a provided class type
 *
 * @param <T> the type of the created class
 */
public class NamedPropertiesLoader<T> {

    private Class<T> type;
    private PropertyTypeAdapter[] adapters;

    /**
     * Creates a new instance of the named property loader, providing the type of config it will load
     *
     * @param type the type
     */
    public NamedPropertiesLoader(Class<T> type) {
        this(type, new PropertyTypeAdapter[0]);
    }

    /**
     * Creates a new instance of the named property loader, providing the type of config it will load
     *
     * @param type the type
     * @param template the template this loader is based on
     */
    public NamedPropertiesLoader(Class<T> type, NamedPropertyLoaderTemplate template) {
        this(type, template.getAdapters());
    }

    /**
     * Creates a new instance of the named property loader, providing the type of config it will load
     *
     * @param type the type
     * @param adapters the adapters that this loader has access too
     */
    public NamedPropertiesLoader(Class<T> type, PropertyTypeAdapter[] adapters) {
        this.type = type;
        this.adapters = adapters;
    }

    /**
     * Loads the provided properties instance into a new instance of the provided
     *
     * @param properties the properties instance to pull the values from
     * @return the created instance
     * @throws IllegalArgumentException if the passed on config type does not provide one, and only one
     * constructor annotated with {@link NamedPropertyConstructor}
     * @throws IllegalArgumentException if a string value found in the properties file could not be converted to the requested type
     * @throws IllegalArgumentException if the annotated constructor defines parameters without a {@link NamedProperty} annotation
     * @throws IllegalArgumentException if the provided type is an enclosed type and needs the instance of the enclosing class to be instantiated
     */
    public T load(Properties properties) throws IllegalArgumentException {
        Validate.isTrue(this.type.getEnclosingClass() == null || Modifier.isStatic(this.type.getModifiers())
                , "The provided class is an enclosed class without a static modifier and cannot be instantiated");

        List<Constructor<?>> foundConstructors = Arrays.stream(type.getConstructors()).filter(c -> c.isAnnotationPresent(NamedPropertyConstructor.class)).collect(Collectors.toList());
        Validate.isTrue(foundConstructors.size() == 1, "The provided type {} does define {} constructor with the " +
                "NamedPropertyConstructor annotation but can only define 1", type.getName(), foundConstructors.size());

        Constructor<T> constructor;
        try {
            constructor = type.getConstructor(foundConstructors.get(0).getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The annotated constructor could not be found", e);
        }

        Parameter[] constructorParameters = constructor.getParameters();
        Object[] providedValues = new Object[constructorParameters.length];

        for (int i = 0; i < constructorParameters.length; i++) {
            Parameter parameter = constructorParameters[i];
            Validate.isTrue(parameter.isAnnotationPresent(NamedProperty.class), "The constructor parameter {} on the " +
                    "type {} does not have a NamedProperty annotation", parameter.getName(), type.getName());

            NamedProperty parameterAnnotation = parameter.getAnnotation(NamedProperty.class);
            Class<?> parameterType = parameter.getType();

            String propertyValue = properties.getProperty(parameterAnnotation.value());
            if (propertyValue == null) propertyValue = parameterAnnotation.defaultValue();
            if (propertyValue.isEmpty()) {
                providedValues[i] = Primitives.isWrapperType(parameterType) ? null : Defaults.defaultValue(parameterType);
                continue;
            }

            try {
                Object value = convert(propertyValue, parameterType);

                if (value == null) { //Check for adapters
                    for (PropertyTypeAdapter adapter : this.adapters) {
                        if (Objects.equals(adapter.getConvertingType(), parameterType)) {
                            value = adapter.convert(propertyValue);
                            break;
                        }
                    }
                }

                providedValues[i] = value;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("The found string " + propertyValue + " was expected to be a " + parameterType.getSimpleName() + " but could not be parsed");
            }
        }

        try {
            return constructor.newInstance(providedValues);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("The provided type's constructor could not be called", e);
        }
    }

    /**
     * Converts a given string into the provided type by parsing it
     *
     * @param input the input string
     * @param type the type to convert it into
     * @return the converted value
     * @throws NumberFormatException if the type was a primitive number but the string could not be parsed
     */
    public Object convert(String input, Class<?> type) throws NumberFormatException {
        if (Primitives.isWrapperType(type)) type = Primitives.unwrap(type);

        if (Objects.equals(byte.class, type)) return Byte.parseByte(input);
        if (Objects.equals(short.class, type)) return Short.parseShort(input);
        if (Objects.equals(int.class, type)) return Integer.parseInt(input);
        if (Objects.equals(float.class, type)) return Float.parseFloat(input);
        if (Objects.equals(double.class, type)) return Double.parseDouble(input);
        if (Objects.equals(long.class, type)) return Long.parseLong(input);
        if (Objects.equals(String.class, type)) return input;

        if (type.isEnum()) return Enum.valueOf(type.asSubclass(Enum.class), input.toUpperCase().replaceAll(" ", "_"));
        return null;
    }
}
