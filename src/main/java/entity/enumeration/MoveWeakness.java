package entity.enumeration;

/**
 * This enum defines possible move weaknesses for an entity in combat.
 */
public enum MoveWeakness {
    PHYSICAL("Physical"),
    MAGIC("Magic"),
    NONE("None");

    public final String label;

    private MoveWeakness(String label) {

        this.label = label;
    }
}
