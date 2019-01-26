package me.lynxplay.alton.utils.properties;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify a constructor parameter of a property config representation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface NamedProperty {

    /**
     * Defines the property name of the provided variable type
     *
     * @return the key
     */
    String value();

    /**
     * Defines the default value that will be provided to the constructor.
     * Passing an empty string, or using the default implementation for that matter, will result in a null instance being passed on
     *
     * @return the defined value
     */
    String defaultValue() default "";
}
