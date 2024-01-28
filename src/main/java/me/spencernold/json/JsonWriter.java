package me.spencernold.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.spencernold.json.components.JsonComponent;

/**
 * Writes objects into the Json format.
 * 
 * @author Spencer Nold
 * @version 1.0.0
 */
public class JsonWriter {

	private final boolean nullOnUnsafe;

	/**
	 * Creates an instance of a JsonWriter with a parameter which determines if an
	 * error should be fatal or not. The default is true, that an error will not be
	 * fatal.
	 * 
	 * @param nullOnUnsafe whether components should be null or throw an exception
	 *                     should an error occur
	 */
	public JsonWriter(boolean nullOnUnsafe) {
		this.nullOnUnsafe = nullOnUnsafe;
	}

	public JsonWriter() {
		this(true);
	}

	/**
	 * Writes an object into the JavaScript Object Notation format.
	 * 
	 * @param object object to be written
	 * @return Json representation of this input object
	 */
	public String write(Object object) {
		if (object == null)
			return "null";
		if (object instanceof JsonComponent)
			return object.toString();
		else if (object instanceof String || object instanceof Character)
			return String.format("\"%s\"", String.valueOf(object));
		else if (object instanceof Boolean || object instanceof Number)
			return String.valueOf(object);
		else if (object instanceof Map)
			return writeMap((Map<?, ?>) object);
		else if (object instanceof Collection<?>)
			return writeCollection((Collection<?>) object);
		else if (object.getClass().isArray())
			return writeArray(object);
		return writeObject(object);
	}

	private String writeCollection(Collection<?> collection) {
		int size = collection.size();
		String[] values = new String[size];
		int index = 0;
		for (Object object : collection) {
			values[index] = write(object);
			index++;
		}
		return "[" + String.join(", ", values) + "]";
	}

	private String writeArray(Object array) {
		int size = Array.getLength(array);
		String[] values = new String[size];
		for (int i = 0; i < size; i++)
			values[i] = write(Array.get(array, i));
		return "[" + String.join(", ", values) + "]";
	}

	private String writeMap(Map<?, ?> map) {
		int size = map.size();
		String[] values = new String[size];
		int index = 0;
		for (Entry<?, ?> entry : map.entrySet()) {
			values[index] = String.format("\"%s\": %s", entry.getKey(), write(entry.getValue()));
			index++;
		}
		return "{" + String.join(", ", values) + "}";
	}

	private String writeObject(Object object) {
		Class<?> clazz = object.getClass();
		List<String> values = new ArrayList<>();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(JsonIgnore.class))
				continue;
			String name = field.getName();
			Object value = accessFieldUnsafe(field, object);
			values.add(String.format("\"%s\": %s", name, write(value)));
		}
		return "{" + String.join(", ", values) + "}";
	}

	private Object accessFieldUnsafe(Field field, Object object) {
		try {
			field.setAccessible(true);
			return field.get(object);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			if (nullOnUnsafe)
				return null;
			throw new JsonException(e.getMessage());
		}
	}
}
