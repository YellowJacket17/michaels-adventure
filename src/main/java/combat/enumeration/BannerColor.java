package combat.enumeration;

/**
 * This enum defines allowable colors for life/skill banner backgrounds displayed during combat.
 */
public enum BannerColor {

    /**
     * Default banner color.
     */
    STANDARD,

    /**
     * Color when banner's respective entity is being considered as a move target.
     */
    TARGET,

    /**
     * Color when a banner's respective entity is taking its turn.
     */
    TURN
}
