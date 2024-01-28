package me.spencernold.json;

/**
 * Thrown when JsonReader or JsonWriter runs into an issue or problem.
 * 
 * @author Spencer Nold
 * @version 1.0.0
 */
public class JsonException extends RuntimeException {

	private static final long serialVersionUID = 5407884631754823458L;

	/**
	 * Default exception constructor
	 * 
	 * @param message message of the exception
	 */
	public JsonException(String message) {
		super(message);
	}
}
