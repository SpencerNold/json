package me.spencernold.json;

/**
 * Java does not support tuples or any other way of multiple return values, so
 * this is a generic data structure for connecting two generic reference types
 * in memory.
 * 
 * @author Spencer Nold
 * @version 1.0.0
 *
 * @param <K> key
 * @param <V> value
 */
class Pair<K, V> {

	private K key;
	private V value;

	Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	Pair() {
	}

	K getKey() {
		return key;
	}

	void setKey(K key) {
		this.key = key;
	}

	V getValue() {
		return value;
	}

	void setValue(V value) {
		this.value = value;
	}
}
