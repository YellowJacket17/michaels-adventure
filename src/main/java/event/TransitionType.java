package event;

/**
 * This enum defines transition types to be used when fading the screen to black for loading.
 */
public enum TransitionType {

    /**
     * Default state.
     */
    DEFAULT,

    /**
     * Warp transition (loading to a new map, for example).
     */
    WARP,

    /**
     * Entering combat transition.
     */
    ENTER_COMBAT,

    /**
     * Exiting combat transition.
     */
    EXIT_COMBAT
}
