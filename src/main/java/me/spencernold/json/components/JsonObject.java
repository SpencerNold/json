package me.spencernold.json.components;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class JsonObject extends JsonComponent {

	private final Map<String, JsonComponent> children = new HashMap<>();
	
	public void set(String key, JsonComponent component) {
		children.put(key, component);
	}
	
	public JsonComponent get(String key) {
		return children.get(key);
	}
	
	public int length() {
		return children.size();
	}
	
	@Override
	public String toString() {
		int size = children.size();
		String[] values = new String[size];
		int index = 0;
		for (Entry<String, JsonComponent> entry : children.entrySet()) {
			values[index] = String.format("\"%s\": %s", entry.getKey(), entry.getValue().toString());
			index++;
		}
		return "{" + String.join(", ", values) + "}";
	}
}
