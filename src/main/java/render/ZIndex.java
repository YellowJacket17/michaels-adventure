package render;

/**
 * This enum defines layering for drawable rendering.
 */
public enum ZIndex {

    BACK_LAYER   (0),
    CENTER_LAYER (1),
    FRONT_LAYER  (2);

    public final int value;

    private ZIndex(int value) {
        this.value = value;
    }
}
