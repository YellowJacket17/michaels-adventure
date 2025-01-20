package combat;

import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
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

    /**
     * Move ID.
     * To be clear, this ID is NOT unique for each MoveBase instance.
     * Each MoveBase subclass has its own unique ID, and all instances of that subclass share that ID.
     */
    protected final int moveId;

    /**
     * Defines which attributes this move will use from both the source and target entities when calculating damage
     * and/or affects.
     * The role that this move plays in combat is also defined: is this move primarily meant for attacking, or is it
     * primarily meant to boost/deplete attributes of other combating entities?
     */
    protected final MoveCategory category;

    /**
     * Entities that this move can target in respect to the source entity (i.e., the user).
     */
    protected final MoveTargets moveTargets;

    /**
     * Move name.
     */
    protected String name;

    /**
     * Move description.
     */
    protected String description;

    /**
     * Move base power.
     */
    protected int power;

    /**
     * Move base accuracy.
     */
    protected int accuracy;

    /**
     * Skill points required to use this move.
     */
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

    public int getAccuracy() {
        return accuracy;
    }

    public int getSkillPoints() {
        return skillPoints;
    }
}
