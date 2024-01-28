package me.spencernold.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * A data structure which represents the type of objects with and without
 * generic type parameters.
 * 
 * @author Spencer Nold
 * @version 1.0.0
 *
 * @param <T> generic type of the defined type
 */
public class TypeDef<T> {

	private final Class<T> rawType;
	private final Type type;

	private TypeDef(Class<T> rawType, Type type) {
		this.rawType = rawType;
		this.type = type;
	}

	public Class<?> getRawType() {
		return rawType;
	}

	public Type getType() {
		return type;
	}

	public T cast(Object obj) {
		return rawType.cast(obj);
	}

	public static <T> TypeDef<T> of(Class<T> clazz, Type... typeArguments) {
		return new TypeDef<>(clazz, new ParameterizedTypeImpl(clazz, null, typeArguments));
	}

	public static <T> TypeDef<T> ofList(Class<T> clazz, Class<T> listType) {
		return of(clazz, listType);
	}

	public static <T> TypeDef<T> ofMap(Class<T> clazz, Class<T> mapType) {
		return of(clazz, String.class, mapType);
	}

	private static class ParameterizedTypeImpl implements ParameterizedType {

		private final Type rawType, ownerType;;
		private final Type[] typeArguments;

		public ParameterizedTypeImpl(Type rawType, Type ownerType, Type... typeArguments) {
			this.rawType = rawType;
			this.ownerType = ownerType;
			this.typeArguments = typeArguments;
		}

		@Override
		public Type[] getActualTypeArguments() {
			return typeArguments;
		}

		@Override
		public Type getRawType() {
			return rawType;
		}

		@Override
		public Type getOwnerType() {
			return ownerType;
		}

	}
}
