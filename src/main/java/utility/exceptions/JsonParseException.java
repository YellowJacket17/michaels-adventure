package utility.exceptions;

/**
 * Custom exception to throw when loading and parsing JSON files fails.
 */
public class JsonParseException extends RuntimeException {

    public JsonParseException(String errorMessage) {

        super(errorMessage);
    }
}
