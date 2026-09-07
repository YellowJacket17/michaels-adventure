package combat;

import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import core.GamePanel;
import entity.enumeration.EntityStatus;
import org.joml.Vector3f;

import java.util.HashMap;

/**
 * This abstract class defines base logic for a move (i.e., skill) in combat.
 */
public abstract class MoveBase {

    /*
     * A move (i.e., skill) represents an action that an entity can take during combat.
     *
     * Each type of Move (i.e., each Move subclass) has a unique ID defined in its subclass definition.
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
     * Defines whether this move will apply direct damage with a possible secondary effect ('PHYSICAL' or 'MAGIC'), or
     * only apply an effect ('SUPPORT').
     */
    protected final MoveCategory category;

    /**
     * Entities that this move can target in respect to the source entity (i.e., the user).
     */
    protected final MoveTargets moveTargets;

    /**
     * Boolean setting whether this move will hit all possible move targets, or whether a single target of the possible
     * move targets must be selected.
     */
    protected final boolean hitAllTargets;

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
     * If the move category is 'SUPPORT', then this value will not be used to apply direct damage.
     */
    protected int power;

    /**
     * Move base accuracy (from zero to one hundred, both inclusive).
     * If the move category is 'SUPPORT', then this value will be ignored and always assumed to be one hundred.
     */
    protected int accuracy;

    /**
     * Skill points required to use this move.
     */
    protected int skillPoints;

    /**
     * Effect animation color (i.e., particle effect color, flash color) (r, g, b).
     */
    protected Vector3f effectColor;

    /**
     * Sound effect (plays when move is used).
     */
    protected String soundEffect;

    /**
     * Boolean setting whether this move will use a flash animation (true) or not (false) in combat.
     */
    protected boolean flashAnimation = false;

    /**
     * Boolean setting whether this move has a higher critical hit chance.
     */
    protected boolean highCriticalHit = false;

    /**
     * Boolean setting whether this move will ignore the target's attribute buffs when calculating damage.
     */
    protected boolean ignoreAttributeBuffs = false;

    /**
     * Boolean setting whether this move will always do damage equal to its power.
     */
    protected boolean damageEqualPower = false;

    /**
     * Color of effects for attribute increases in combat.
     */
    public static final Vector3f ATTRIBUTE_INCREASE_COLOR = new Vector3f(166, 255, 168);

    /**
     * Color of effects for attribute decreases in combat.
     */
    public static final Vector3f ATTRIBUTE_DECREASE_COLOR = new Vector3f(255, 166, 190);

    /**
     * Color of effects for skill recovery in combat.
     */
    public static final Vector3f SKILL_RECOVERY_COLOR = new Vector3f(188, 166, 255);

    /**
     * Color of effects for physical moves in combat.
     */
    public static final Vector3f PHYSICAL_MOVE_COLOR = new Vector3f(166, 219, 255);

    /**
     * Color of effects for magic moves in combat.
     */
    public static final Vector3f MAGIC_MOVE_COLOR = new Vector3f(255, 244, 166);

    /**
     * Color of effects for support moves in combat.
     */
    public static final Vector3f SUPPORT_MOVE_COLOR = new Vector3f(255, 255, 255);

//    /**
//     * Color for ice blue effects in combat.
//     */
//    public static final Vector3f ICE_BLUE_COLOR = new Vector3f(166, 253, 255);
//
//    /**
//     * Color for orange effects in combat.
//     */
//    public static final Vector3f ORANGE_COLOR = new Vector3f(255, 200, 166);
//
//    /**
//     * Color for pink effects in combat.
//     */
//    public static final Vector3f PINK_COLOR = new Vector3f(244, 166, 255);
//
//    /**
//     * Color for silver effects in combat.
//     */
//    public static final Vector3f SILVER_COLOR = new Vector3f(204, 227, 242);
//
//    /**
//     * Color for brown effects in combat.
//     */
//    public static final Vector3f BROWN_COLOR = new Vector3f(219, 202, 204);
//
//    /**
//     * Color for black effects in combat.
//     */
//    public static final Vector3f BLACK_COLOR = new Vector3f(139, 139, 139);


    // CONSTRUCTOR
    /**
     * Constructs an MoveBase instance.
     *
     * @param gp GamePanel instance
     * @param moveId move ID (unique to each subclass)
     * @param category type of move (physical, magic, or support)
     * @param moveTargets combating entities that may be targeted by this move
     * @param hitAllTargets whether this move will target all possible move targets (true) or only target a single
     *                        target of the possible move targets (false)
     */
    public MoveBase(GamePanel gp, int moveId, MoveCategory category, MoveTargets moveTargets, boolean hitAllTargets) {
        this.gp = gp;
        this.moveId = moveId;
        this.category = category;
        this.moveTargets = moveTargets;
        this.hitAllTargets = hitAllTargets;
    }


    // METHODS
    /**
     * Runs move effect logic.
     * Note that the 'progressCombat()' method in CombatManager should NOT be called somewhere through this method's
     * logic to hand off control to the next queued action.
     * This will be called automatically in Act_UseMove (support) or CombatAnimationSupport (non-support).
     *
     * @param sourceEntityId ID of entity using move
     * @param targetEntityDeltaLife Calculated change in life points (positive for decrease, negative for increase) of
     *                              each targeted entity; entity ID is the key, life points is the value.
     */
    public abstract void runEffects(int sourceEntityId, HashMap<Integer, Integer> targetEntityDeltaLife);


    /**
     * Verifies whether an entity is eligible to be targeted by this move.
     * In other words, this method sets the conditions that a target must meet in order to be a selectable target for
     * this move.
     * By default, any non-fainted entity may be targeted.
     * Each MoveBase implementation may override this default behavior with unique conditions.
     *
     * @param targetEntityId ID of candidate target entity
     * @return whether the candidate entity is a valid target (true) or not (false)
     */
    public boolean verifyTarget(int targetEntityId) {

        if (gp.getEntityM().getEntityById(targetEntityId).getStatus() != EntityStatus.FAINT) {

            return true;
        }
        return false;
    }


    /**
     * Calculates any bonus damage that should be applied when using a move (e.g., a random number is selected when
     * each time a move to determine how powerful it will be).
     * By default, no bonus damage will be applied.
     * Each MoveBase implementation may override this default behavior with unique calculations.
     *
     * @param sourceEntityId ID of entity using the move
     * @param targetEntityId ID of target entity
     * @return change in life points of the targeted entity due to bonus damage
     */
    public int calculateBonusDamage(int sourceEntityId, int targetEntityId) {

        return 0;
    }


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

    public boolean isHitAllTargets() {
        return hitAllTargets;
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

    public Vector3f getEffectColor() {
        return effectColor;
    }

    public String getSoundEffect() {
        return soundEffect;
    }

    public boolean isFlashAnimation() {
        return flashAnimation;
    }

    public boolean isHighCriticalHit() {
        return highCriticalHit;
    }

    public boolean isIgnoreAttributeBuffs() {
        return ignoreAttributeBuffs;
    }

    public boolean isDamageEqualPower() {
        return damageEqualPower;
    }
}
