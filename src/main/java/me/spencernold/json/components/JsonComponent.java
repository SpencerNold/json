package me.spencernold.json.components;

import me.spencernold.json.JsonException;

public abstract class JsonComponent {

	public final boolean isPrimitive() {
		return this instanceof JsonPrimitive;
	}
	
	public final boolean isArray() {
		return this instanceof JsonArray;
	}
	
	public final boolean isObject() {
		return this instanceof JsonObject;
	}

	public final JsonObject getAsObject() {
		if (this instanceof JsonObject)
			return (JsonObject) this;
		throw new JsonException("component is not an object");
	}
	
	public final JsonArray getAsArray() {
		if (this instanceof JsonArray)
			return (JsonArray) this;
		throw new JsonException("component is not an array");
	}
	
	public final JsonPrimitive getAsPrimitive() {
		if (this instanceof JsonPrimitive)
			return (JsonPrimitive) this;
		throw new JsonException("component is not a primitive");
	}
}
