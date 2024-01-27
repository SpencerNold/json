package me.spencernold.json.components;

import me.spencernold.json.JsonException;

public final class JsonPrimitive extends JsonComponent {

	private Object value;
	
	public JsonPrimitive(Object value) {
		this.value = validate(value);
	}
	
	private Object validate(Object object) {
		if (object == null)
			return null;
		if (!(object instanceof String) && !(object instanceof Number) && !(object instanceof Boolean))
			throw new JsonException("unsupported primitive type: " + object.getClass().getName());
		return object;
	}
	
	public Object getRawValue() {
		return value;
	}
	
	public String getStringValue() {
		if (value instanceof String)
			return (String) value;
		throw new JsonException("component is not a String");
	}
	
	public boolean getBooleanValue() {
		if (value instanceof Boolean)
			return (boolean) value;
		throw new JsonException("component is not a Boolean");
	}
	
	public int getIntValue() {
		if (value instanceof Number)
			return (int) (value instanceof Long ? getLongValue() : getDoubleValue());
		throw new JsonException("component is not a Number");
	}
	
	public long getLongValue() {
		if (value instanceof Number)
			return (long) (value instanceof Long ? value : (double) value);
		throw new JsonException("component is not a Number");
	}
	
	public float getFloatValue() {
		if (value instanceof Number)
			return (float) (value instanceof Float ? getFloatValue() : getLongValue());
		throw new JsonException("component is not a Number");
	}
	
	public double getDoubleValue() {
		if (value instanceof Number)
			return (double) (value instanceof Double ? value : (long) value);
		throw new JsonException("component is not a Number");
	}
	
	@Override
	public String toString() {
		return value instanceof String ? String.format("\"%s\"", value) : String.valueOf(value);
	}
}
