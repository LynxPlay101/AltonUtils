package me.lynxplay.alton.utils.properties;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark the constructor that is used to instantiated the property class
 * <p>
 * The constructor can contain primitive values (byte,short,int,float,double,long), enum types or strings
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
@Documented
public @interface NamedPropertyConstructor {

    /**
     * The strict parameter describes whether the construction of the config instance will fail
     * if values have been set in the properties file that cannot be mapped to this constructor
     *
     * @return the flag
     */
    boolean strict() default false;

}
