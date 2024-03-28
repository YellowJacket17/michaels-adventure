package combat;

import core.GamePanel;

import java.util.List;

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
    protected final MoveTargets moveTargets;
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
     * @param category type of move (PHYSICAL or MAGIC)
     * @param moveTargets combating entities that may be targeted by this move
     */
    public MoveBase(GamePanel gp, int moveId, MoveCategory category, MoveTargets moveTargets) {
        this.gp = gp;
        this.moveId = moveId;
        this.category = category;
        this.moveTargets = moveTargets;
    }


    // METHOD
    /**
     * Runs move effect logic.
     */
    public abstract void runEffects(int sourceEntityId, List<Integer> targetEntityId);


    // GETTERS
    public int getMoveId() {
        return moveId;
    }

    public MoveCategory getCategory() {
        return category;
    }

    public MoveTargets getMoveTargets() {
        return moveTargets;
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
    public void setSkillPoints(int skillPoints) {
        if (skillPoints >= 0) {
            this.skillPoints = skillPoints;
        }
    }
}
