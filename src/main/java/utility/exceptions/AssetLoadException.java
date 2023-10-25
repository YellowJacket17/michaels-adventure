package utility.exceptions;

/**
 * Custom exception to throw when loading an asset fails.
 */
public class AssetLoadException extends RuntimeException {

    public AssetLoadException(String errorMessage) {

        super(errorMessage);
    }
}
