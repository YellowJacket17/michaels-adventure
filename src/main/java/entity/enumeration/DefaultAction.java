package entity.enumeration;

/**
 * This enum defines the possible default actions an entity can have.
 */
public enum DefaultAction {

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
