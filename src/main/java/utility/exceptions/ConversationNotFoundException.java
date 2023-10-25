package utility.exceptions;

/**
 * Custom exception to throw when a conversation is not found.
 */
public class ConversationNotFoundException extends RuntimeException {

    public ConversationNotFoundException(String errorMessage) {

        super(errorMessage);
    }
}
