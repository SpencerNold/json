package me.spencernold.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This should be attached to fields that you want to be skipped when reading
 * and writing Json to and from Object instances.
 * 
 * @author Spencer Nold
 * @version 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonIgnore {

}
