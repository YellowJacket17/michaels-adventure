package entity.enumeration;

/**
 * This enum defines the possible default idle actions an entity can have.
 */
public enum DefaultIdleAction {

    /**
     * Stand still, facing one direction.
     */
    STATIC,

    /**
     * Sporadically take a step in a random direction.
     */
    RANDOM_STEPS,

    /**
     * Stand still, sporadically changing to face a random direction.
     */
    RANDOM_TURNS
}
