package combat;

import core.GamePanel;

/**
 * This abstract class defines base logic for a move in combat.
 */
public abstract class MoveBase {

    /*
     * A move represents an action that an entity can take during combat.
     *
     * Each type of Move (i.e., each Attack subclass) has a unique ID defined in its subclass definition.
     * This ID is NOT unique to each Move instance; all Move instances of the same subclass share the same ID.
     */

    // FIELDS
    protected final GamePanel gp;
    protected final int moveId;
    protected final MoveCategory category;
    protected String name;
    protected String description;
    protected int power;
    protected int skillPoints;


    // CONSTRUCTOR
    /**
     * Constructs an MoveBase instance.
     *
     * @param gp GamePanel instance
     * @param moveId move ID
     * @param category type of attack (PHYSICAL or MAGIC)
     */
    public MoveBase(GamePanel gp, int moveId, MoveCategory category) {
        this.gp = gp;
        this.moveId = moveId;
        this.category = category;
    }


    // METHOD
    // TODO : Perhaps have arguments to this method be everything we would need to apply move effects (steal HP, skip next turn, apply buffs, etc.).
    //  This input could be the move user, the move target(s), and some additional information.
    //  Also, should this method be called `runEffects()` or something else?
    /**
     * Runs move effect logic.
     */
    public abstract void runEffects();


    // GETTERS
    public int getMoveId() {
        return moveId;
    }

    public MoveCategory getCategory() {
        return category;
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
