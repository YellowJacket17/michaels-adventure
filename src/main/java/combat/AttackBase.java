package combat;

import core.GamePanel;

/**
 * This abstract class defines base logic for an attack.
 */
public abstract class AttackBase {

    /*
     * An attack represents a move that an entity can make during combat.
     *
     * Each type of Attack (i.e., each Attack subclass) has a unique ID defined in its subclass definition.
     * This ID is NOT unique to each Attack instance; all Attack instances of the same subclass share the same ID.
     */

    // FIELDS
    protected final GamePanel gp;
    protected final int attackId;
    protected final AttackType type;
    protected String name;
    protected String description;
    protected int power;
    protected int skillPoints;


    // CONSTRUCTOR
    /**
     * Constructs an ActionBase instance.
     *
     * @param gp GamePanel instance
     * @param attackId attack ID
     * @param type type of attack (PHYSICAL or MAGIC)
     */
    public AttackBase(GamePanel gp, int attackId, AttackType type) {
        this.gp = gp;
        this.attackId = attackId;
        this.type = type;
    }


    // Getters.
    public int getAttackId() {
        return attackId;
    }

    public AttackType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPower() {
        return power;
    }

    public int getSkillPoints() {
        return skillPoints;
    }


    // Setters.
    public void setPower(int power) {
        if (power >= 0) {
            this.power = power;
        }
    }

    public void setSkillPoints(int skillPoints) {
        if (skillPoints >= 0) {
            this.skillPoints = skillPoints;
        }
    }
}
