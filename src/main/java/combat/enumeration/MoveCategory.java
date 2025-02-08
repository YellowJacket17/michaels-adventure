package combat.enumeration;

/**
 * This enum defines the possible categories that a combat move can be.
 */
public enum MoveCategory {

    /**
     * This move uses the source entity's attack attribute and the target entities' defense attributes to calculate
     * damage/affects.
     */
    PHYSICAL,

    /**
     * This move uses the source entity's magic attribute and the target entities' magic attributes to calculate
     * damage/affects.
     */
    MAGIC,

    /**
     * This move does not apply damage to target entities and any affects do not depend on any source/target entity
     * attributes (ex. always raises a target entity's agility attribute by a set amount, or always heals a target
     * entity by/up to 50% of their maximum life).
     */
    SUPPORT
}
