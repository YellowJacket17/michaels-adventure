package combat.enumeration;

/**
 * This enum defines the possible categories that a combat move can be.
 */
public enum MoveCategory {

    /**
     * This move uses the source entity's attack attribute and the target entity's defense attribute to calculate and
     * apply direct damage.
     * A secondary effect may also be applied.
     */
    PHYSICAL,

    /**
     * This move uses the source entity's magic attribute and the target entity's magic attribute to calculate and
     * apply direct damage.
     * A secondary effect may also be applied.
     */
    MAGIC,

    /**
     * This move applies an effect to a target entity (revive, modify attributes, etc.).
     * Direct damage is not applied to the target entity.
     */
    SUPPORT
}
