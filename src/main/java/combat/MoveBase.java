package combat;

import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import core.GamePanel;
import entity.enumeration.EntityStatus;
import org.joml.Vector3f;

import java.util.ArrayList;
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
     * Defines whether this move will apply direct damage with a possible secondary effect (`PHYSICAL` or `MAGIC`), or
     * only apply an effect (`SUPPORT`).
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
     * If the move category is `SUPPORT`, then this value will not be used to apply direct damage.
     */
    protected int power;

    /**
     * Move base accuracy (from zero to one hundred, both inclusive).
     * If the move category is `SUPPORT`, then this value will be ignored and always assumed to be one hundred.
     */
    protected int accuracy;

    /**
     * Skill points required to use this move.
     */
    protected int skillPoints;

    /**
     * Particle effect color (i.e., splash animation color on target entities).
     */
    protected Vector3f particleEffectColor;

    /**
     * Sound effect (plays when move is used).
     */
    protected String soundEffect;


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
     */
    public abstract void runEffects(int sourceEntityId, ArrayList<Integer> targetEntityId);


    /**
     * Verifies whether an entity is eligible to be targeted by this move.
     * In other words, this method sets the conditions that a target must meet in order to be a selectable target for
     * this move.
     * By default, any non-fainted entity may be targeted.
     * Each MoveBase implementation may override this default behavior with unique conditions.
     *
     * @param entityId ID of candidate target entity
     * @return whether the candidate entity is a valid target (true) or not (false)
     */
    public boolean verifyTarget(int entityId) {

        if (gp.getEntityM().getEntityById(entityId).getStatus() != EntityStatus.FAINT) {

            return true;
        }
        return false;
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

    public Vector3f getParticleEffectColor() {
        return particleEffectColor;
    }

    public String getSoundEffect() {
        return soundEffect;
    }
}
