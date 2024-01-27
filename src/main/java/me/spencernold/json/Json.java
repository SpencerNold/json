package me.spencernold.json;

import me.spencernold.json.components.JsonComponent;

public class Json {

	private static final JsonWriter WRITER = new JsonWriter(true);
	private static final JsonReader READER = new JsonReader(true);
	
	public static String toJson(Object object) {
		try {
			return WRITER.write(object);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static <T> T fromJson(String json, Class<T> type) {
		try {
			return READER.read(json, type);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static <T> T fromJson(String json, TypeDef<T> type) {
		try {
			return READER.read(json, type);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static JsonComponent fromJson(String json) {
		return fromJson(json, JsonComponent.class);
	}
}
