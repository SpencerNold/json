package me.spencernold.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.spencernold.json.components.JsonArray;
import me.spencernold.json.components.JsonComponent;
import me.spencernold.json.components.JsonObject;
import me.spencernold.json.components.JsonPrimitive;

public class JsonReader {

	private final boolean nullOnUnsafe;

	public JsonReader(boolean nullOnUnsafe) {
		this.nullOnUnsafe = nullOnUnsafe;
	}

	public JsonReader() {
		this(true);
	}

	public <T> T read(String json, Class<T> clazz) {
		return read(json, TypeDef.of(clazz));
	}

	public <T> T read(String json, TypeDef<T> type) {
		Object object = read(json, type.getRawType(), type.getType());
		return object == null ? null : type.cast(object);
	}

	private Object read(String json, Class<?> rawType, Type type) {
		if (json == null) {
			if (nullOnUnsafe)
				return null;
			throw new JsonException("unnable to read json: input is null");
		}
		json = json.replaceAll("\\s+(?=(?:(?:[^\"]*\"){2})*[^\"]*$)", "");
		Pair<Integer, Object> pair = parseBranch(json, rawType, type);
		return pair.getValue();
	}

	private Pair<Integer, Object> parseBranch(String json, Class<?> rawType, Type type) {
		int len = json.length();
		char c = json.charAt(0);
		if ((c == '"' || c == '\'') && (JsonComponent.class.isAssignableFrom(rawType) || isString(rawType)))
			return parseString(json, rawType, 0, len);
		else if (c == '-'
				|| (c >= '0' && c <= '9') && (JsonComponent.class.isAssignableFrom(rawType) || isNumeric(rawType)))
			return parseNumber(json, rawType, 0, len);
		else if (c == 'n')
			return new Pair<>(4,
					JsonComponent.class.isAssignableFrom(rawType) ? null : Reflection.getDefaultValue(rawType));
		else if ((c == 'f' || c == 't') && (JsonComponent.class.isAssignableFrom(rawType) || isBoolean(rawType)))
			return parseBoolean(json, rawType, 0, len);
		else if (c == '[' && (JsonComponent.class.isAssignableFrom(rawType)
				|| Collection.class.isAssignableFrom(rawType) || rawType.isArray()))
			return parseArray(json, rawType, type, 0, len);
		else if (c == '{')
			return parseObject(json, rawType, type, 0, len);
		else {
			System.out.println(c);
			if (nullOnUnsafe)
				return null;
			throw new JsonException("parsing type mismatch for " + rawType.getName());
		}
	}

	private Pair<Integer, Object> parseString(String json, Class<?> type, int index, int length) {
		int start = ++index;
		for (; index < length; index++) {
			char c = json.charAt(index);
			if (c == '"' || c == '\'')
				break;
		}
		String sub = json.substring(start, index);
		int size = sub.length() + 2;
		if (JsonComponent.class.isAssignableFrom(type))
			return new Pair<>(size, new JsonPrimitive(sub));
		Object object = type == String.class ? sub : sub.charAt(0);
		return new Pair<>(size, object);
	}

	private Pair<Integer, Object> parseNumber(String json, Class<?> type, int index, int length) {
		int start = index;
		for (++index; index < length; index++) {
			char c = json.charAt(index);
			if (c == ' ' || c == ',')
				break;
		}
		String sub = json.substring(start, index);
		int size = sub.length();
		Object number = null;
		if (sub.contains("."))
			number = Double.parseDouble(sub);
		else
			number = Long.parseLong(sub);
		if (JsonComponent.class.isAssignableFrom(type))
			return new Pair<>(size, new JsonPrimitive(number));
		return new Pair<>(size, number); // TODO Ensure that number and type are the same
	}

	private Pair<Integer, Object> parseBoolean(String json, Class<?> type, int index, int length) {
		int start = index;
		for (++index; index < length; index++) {
			char c = json.charAt(index);
			if (c == ' ' || c == ',')
				break;
		}
		String sub = json.substring(start, index);
		int size = sub.length();
		try {
			boolean bool = Boolean.parseBoolean(sub);
			if (JsonComponent.class.isAssignableFrom(type))
				return new Pair<>(size, new JsonPrimitive(bool));
			return new Pair<>(size, bool);
		} catch (Exception e) {
			if (nullOnUnsafe)
				return new Pair<>(size, false);
			throw new JsonException("unable to read boolean type: " + sub);
		}
	}

	@SuppressWarnings("unchecked")
	private Pair<Integer, Object> parseArray(String json, Class<?> rawType, Type type, int index, int length) {
		index++;
		List<Object> temporary = new ArrayList<>();
		int size = 0;
		for (; index < length; index++) {
			if (json.charAt(index) == ']')
				break;
			Pair<Integer, Object> value = null;
			if (JsonComponent.class.isAssignableFrom(rawType)) {
				value = parseBranch(json.substring(index, length), JsonComponent.class, JsonComponent.class);
				temporary.add(value.getValue());
			} else if (rawType.isArray()) {
				value = parseBranch(json.substring(index, length), rawType.getComponentType(), rawType.getComponentType());
				temporary.add(value.getValue());
			} else if (Collection.class.isAssignableFrom(rawType)) {
				if (!(type instanceof ParameterizedType)) {
					// TODO Fatal error
					break;
				}
				Type mType = ((ParameterizedType) type).getActualTypeArguments()[0];
				value = parseBranch(json.substring(index, length), (Class<?>) mType, mType); // TODO support lists of lists/maps/etc.
				temporary.add(value.getValue());
			}
			index += value.getKey();
			if (json.charAt(index) == ']')
				break;
		}
		int len = temporary.size();
		Object array = initializePossibleJsonArrayArrayOrCollection(rawType, len);
		for (int i = 0; i < len; i++) {
			if (array instanceof JsonArray)
				((JsonArray) array).add((JsonComponent) temporary.get(i));
			else if (array instanceof Collection)
				((Collection<Object>) array).add(temporary.get(i));
			else
				Array.set(array, i, temporary.get(i));
		}
		return new Pair<>(size, array);
	}
	
	private Object initializePossibleJsonArrayArrayOrCollection(Class<?> clazz, int length) {
		if (JsonComponent.class.isAssignableFrom(clazz))
			return new JsonArray();
		else if (List.class.isAssignableFrom(clazz)) // TODO Add checks for if the list is an ArrayList, LinkedList, etc. first
			return new ArrayList<>(length);
		else if (Set.class.isAssignableFrom(clazz))
			return new HashSet<>(length); // TODO Same thing, except for HashSet, etc.
		else if (clazz.isArray())
			return Array.newInstance(clazz.getComponentType(), length);
		else {
			if (nullOnUnsafe)
				return null;
			throw new JsonException("unsupported collection type: " + clazz.getName());
		}
	}

	@SuppressWarnings("unchecked")
	private Pair<Integer, Object> parseObject(String json, Class<?> rawType, Type type, int index, int length) {
		index++;
		Object object = initializePossibleJsonObjectMapOrOther(rawType);
		int size = 0;
		for (; index < length; index++) {
			if (json.charAt(index) == '}') // Empty objects
				break;
			Pair<Integer, Object> key = parseString(json, String.class, index, length);
			index += key.getKey();
			if (json.charAt(index) != ':') // Malformed object?
				break;
			index++;
			String name = (String) key.getValue();
			size += name.length() + 3;
			Pair<Integer, Object> value;
			if (JsonComponent.class.isAssignableFrom(rawType)) {
				value = parseBranch(json.substring(index, length), JsonComponent.class, JsonComponent.class);
				((JsonObject) object).set(name, (JsonComponent) value.getValue());
			} else if (Map.class.isAssignableFrom(rawType)) {
				if (!(type instanceof ParameterizedType)) {
					// TODO Freak the hell out? This is a fatal error to the program
					break;
				}
				Type mType = ((ParameterizedType) type).getActualTypeArguments()[1];
				value = parseBranch(json.substring(index, length), (Class<?>) mType, mType); // TODO support maps of lists/maps/etc.
				((Map<String, Object>) object).put(name, value.getValue());
			} else {
				try {
					Field field = rawType.getDeclaredField(name);
					value = parseBranch(json.substring(index, length), field.getType(), field.getGenericType());
					field.setAccessible(true);
					try {
						Object fieldValue = value.getValue();
						fieldValue = forceConvertToType(fieldValue, field.getType());
						field.set(object, fieldValue);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						if (nullOnUnsafe)
							continue;
						throw new JsonException(e.getMessage());
					}
				} catch (NoSuchFieldException e) {
					throw new JsonException("unable to find field for: " + name);
				}
			}
			index += value.getKey();
			size += value.getKey();
			if (json.charAt(index) == '}')
				break;
		}
		return new Pair<>(size, object);
	}

	private Object initializePossibleJsonObjectMapOrOther(Class<?> clazz) {
		if (JsonComponent.class.isAssignableFrom(clazz))
			return new JsonObject();
		else if (Map.class.isAssignableFrom(clazz))
			return new HashMap<String, Object>();
		try {
			return Reflection.newInstance(clazz);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			if (nullOnUnsafe)
				return null;
			throw new JsonException(e.getMessage());
		}
	}
	
	private Object forceConvertToType(Object object, Class<?> type) {
		if (object == null)
			return null;
		if (object.getClass() == type)
			return object;
		if (object instanceof Number) {
			Number num = (Number) object;
			if (type == byte.class || type == Byte.class)
				return num.byteValue();
			else if (type == short.class || type == Short.class)
				return num.shortValue();
			else if (type == int.class || type == Integer.class)
				return num.intValue();
			else if (type == long.class || type == Long.class)
				return num.longValue();
			else if (type == float.class || type == Float.class)
				return num.floatValue();
			else if (type == double.class || type == Double.class)
				return num.doubleValue();
		}
		return object; // primitive vs. object wrapper class case (or error case, either way it will be handled elsewhere)
	}

	private boolean isString(Class<?> clazz) {
		return clazz == String.class || clazz == Character.class || clazz == char.class;
	}

	private boolean isNumeric(Class<?> clazz) {
		return Number.class.isAssignableFrom(clazz)
				|| (clazz.isPrimitive() && clazz != boolean.class && clazz != char.class);
	}

	private boolean isBoolean(Class<?> clazz) {
		return clazz == Boolean.class || clazz == boolean.class;
	}
}
