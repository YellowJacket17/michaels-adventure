package utility.exceptions;

/**
 * Custom exception to throw when transferring EntityBase instances from one hash map to another fails.
 */
public class EntityTransferException extends RuntimeException {

    public EntityTransferException(String errorMessage) {

        super(errorMessage);
    }
}
