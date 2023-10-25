package utility.exceptions;

/**
 * Custom exception to throw when staging an audio file fails.
 */
public class AudioStageException extends RuntimeException {

    public AudioStageException(String errorMessage) {

        super(errorMessage);
    }
}
