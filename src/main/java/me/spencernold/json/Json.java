package me.spencernold.json;

import me.spencernold.json.components.JsonComponent;

/**
 * Simplifies the JsonWriter and JsonReader into {@link #toJson(Object)} and
 * {@link #fromJson(String)} functions.
 * 
 * @author Spencer Nold
 * @version 1.0.0
 * @see JsonReader
 * @see JsonWriter
 */
public class Json {

	private static final JsonWriter WRITER = new JsonWriter(true);
	private static final JsonReader READER = new JsonReader(true);

	/**
	 * Writes the object argument into the Json file format. Any errors that occur
	 * will result in either the string or the json component being null. If this is
	 * not your desired intention, use the JsonWriter class.
	 * 
	 * @see JsonWriter
	 * 
	 * @param object object to be written
	 * @return json format of the input object
	 */
	public static String toJson(Object object) {
		try {
			return WRITER.write(object);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Reads the Json argument into an object of type <T>. Any errors that occur
	 * will result in either the field or object being null. If this is not your
	 * desired intention, use the JsonReader class.
	 * 
	 * @param <T>  type of a class, the class may NOT have generic type arguments,
	 *             to read Map and Collection types, use
	 *             {@link #fromJson(String, TypeDef)}
	 * 
	 * @param json Json to be interpreted into type <T>
	 * @param type type to interpret the Json into
	 * @return an object implementation of the <T> type
	 */
	public static <T> T fromJson(String json, Class<T> type) {
		try {
			return READER.read(json, type);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Reads the Json argument into an object of type <T>. Any errors that occur
	 * will result in either the field or object being null. If this is not your
	 * desired intention, use the JsonReader class.
	 * 
	 * @param <T>  type of a class, this function is meant for reading objects which
	 *             have generic type arguments
	 * @param json Json to be interpreted into type <T>
	 * @param type type to interpret the Json into
	 * @return an object implementation of the <T> type
	 */
	public static <T> T fromJson(String json, TypeDef<T> type) {
		try {
			return READER.read(json, type);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Reads the Json argument into an implementation of a JsonComponent. This can
	 * either be a JsonPrimitive, JsonArray, or JsonObject. Any errors that occur
	 * will result in either the field or object being null. If this is not your
	 * desired intention, use the JsonReader class.
	 * 
	 * @param json Json to be interpreted
	 * @return a generic object implementation of the JsonComponent
	 */
	public static JsonComponent fromJson(String json) {
		return fromJson(json, JsonComponent.class);
	}
}
