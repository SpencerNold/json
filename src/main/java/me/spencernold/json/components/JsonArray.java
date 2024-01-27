package me.spencernold.json.components;

import java.util.ArrayList;
import java.util.List;

public final class JsonArray extends JsonComponent {

	private final List<JsonComponent> children = new ArrayList<>();
	
	public void set(int index, JsonComponent component) {
		children.set(index, component);
	}
	
	public void add(JsonComponent component) {
		children.add(component);
	}
	
	public JsonComponent get(int index) {
		return children.get(index);
	}
	
	public int length() {
		return children.size();
	}
	
	@Override
	public String toString() {
		int size = children.size();
		String[] values = new String[size];
		for (int i = 0; i < size; i++)
			values[i] = children.get(i).toString();
		return "[" + String.join(", ", values) + "]";
	}
}
