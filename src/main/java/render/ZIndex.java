package render;

/**
 * This enum defines layering for drawable rendering.
 */
public enum ZIndex {

    THIRD_LAYER(0),
    SECOND_LAYER(1),
    FIRST_LAYER(2);

    public final int value;

    private ZIndex(int value) {
        this.value = value;
    }
}
