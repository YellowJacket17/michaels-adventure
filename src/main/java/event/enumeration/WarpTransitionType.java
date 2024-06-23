package event.enumeration;

/**
 * This enum defines the specific transition types to be used with a warp transition.
 */
public enum WarpTransitionType {

    /**
     * Simple fade to black on the current map and fade from black on the new map.
     */
    BASIC,

    /**
     * Player entity switches to a walking sprite before initiating fade to black on the current map.
     * A fade from black then occurs on the new map with the player entity in an idle sprite.
     */
    STEP_PORTAL
}
