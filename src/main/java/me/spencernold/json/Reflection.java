package me.spencernold.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple reflection utility class
 * 
 * @author Spencer Nold
 * @version 1.0.0
 *
 */
public class Reflection {

	private static final Map<Class<?>, Object> DEFAULT_CLASS_VALUES = new HashMap<Class<?>, Object>() {
		private static final long serialVersionUID = -4021049411374257440L;
		{
			put(boolean.class, (boolean) false);
			put(byte.class, (byte) 0);
			put(short.class, (short) 0);
			put(char.class, (char) '\0');
			put(int.class, (int) 0);
			put(long.class, (long) 0);
			put(float.class, (float) 0.0f);
			put(double.class, (double) 0.0d);
		}
	};

	/**
	 * {@link #newInstance(Class)}
	 * 
	 * @param <T>   type of the Object
	 * @param clazz class to create an instance of
	 * @return an object of type <T> or null should an error occur
	 */
	public static <T> T newInstanceUnsafe(Class<T> clazz) {
		try {
			return newInstance(clazz);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Creates an instance of a class with Java reflection. As the newInstance
	 * function is deprecated, this calls a constructor, filling up any possible
	 * arguments with their default values. Ex. references (Object instances) are
	 * null, ints are 0, etc.
	 * 
	 * @param <T>   type of the Object
	 * @param clazz class to create an instance of
	 * @return an object of type <T>
	 * @throws InstantiationException    @see InstantiationException
	 * @throws IllegalAccessException    @see IllegalAccessException
	 * @throws IllegalArgumentException  @see IllegalArgumentException
	 * @throws InvocationTargetException @see InvocationTargetException
	 */
	public static <T> T newInstance(Class<T> clazz)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
		constructor.setAccessible(true);
		Object[] arguments = generateDefaultValues(constructor.getParameterTypes());
		return clazz.cast(constructor.newInstance(arguments));
	}

	/**
	 * Returns null for references (Object implementation classes), 0 for numeric
	 * types, false for boolean types, etc.
	 * 
	 * @param clazz type to create a default value for
	 * @return default value of a type
	 */
	public static Object getDefaultValue(Class<?> clazz) {
		return DEFAULT_CLASS_VALUES.getOrDefault(clazz, null);
	}

	private static Object[] generateDefaultValues(Class<?>[] classes) {
		Object[] objects = new Object[classes.length];
		for (int i = 0; i < classes.length; i++)
			objects[i] = getDefaultValue(classes[i]);
		return objects;
	}
}
