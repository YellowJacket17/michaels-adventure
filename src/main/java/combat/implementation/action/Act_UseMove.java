package combat.implementation.action;

import combat.ActionBase;
import combat.MoveBase;
import combat.enumeration.MoveCategory;
import core.GamePanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class defines a combat action (use a combat move).
 * Note that if any affected entities faint, further actions will be queued as applicable.
 */
public class Act_UseMove extends ActionBase {

    // FIELDS
    /**
     * Move to be used.
     */
    private final MoveBase move;

    /**
     * ID of the entity using the move.
     */
    private final int sourceEntityId;

    /**
     * IDs of the entities targeted by the move.
     */
    private final List<Integer> targetEntityIds = new ArrayList<>();


    // CONSTRUCTORS
    public Act_UseMove(GamePanel gp, MoveBase move, int sourceEntityId, int targetEntityId) {
        super(gp);
        this.move = move;
        this.sourceEntityId = sourceEntityId;
        this.targetEntityIds.add(targetEntityId);
    }


    public Act_UseMove(GamePanel gp, MoveBase move, int sourceEntityId, ArrayList<Integer> targetEntityIds) {
        super(gp);
        this.move = move;
        this.sourceEntityId = sourceEntityId;
        for (int entityId : targetEntityIds) {
            this.targetEntityIds.add(entityId);
        }
    }


    // METHODS
    @Override
    public void run() {

        HashMap<Integer, Integer> targetEntitiesFinalLife = calculateDamage();                                          // Calculate (but don't apply) final life values of each target entity.
        gp.getCombatAnimationS().initiateStandardMoveAnimation(sourceEntityId, targetEntitiesFinalLife, move);          // Play standard attack animation (fainting + move affects applied afterward).
    }


    /**
     * Calculates damage to all entities targeted by this move.
     * Note that this method does not actually apply damage to an entity.
     *
     * @return final life points of each targeted entity after applying damage; entity ID is the key, life points is the
     * value
     */
    private HashMap<Integer, Integer> calculateDamage() {

        HashMap<Integer, Integer> targetEntitiesFinalLife = new HashMap<>();
        int sourceEntityAttack = gp.getEntityM().getEntityById(sourceEntityId).getAttack();
        int sourceEntityMagic = gp.getEntityM().getEntityById(sourceEntityId).getMagic();
        int targetDamage;
        int targetFinalLife;

        for (int targetEntityId : targetEntityIds) {                                                                    // Calculate and apply damage dealt to each target entity.

            if (move.getCategory() == MoveCategory.PHYSICAL) {

                int targetEntityDefense = gp.getEntityM().getEntityById(targetEntityId).getDefense();
                targetDamage = move.getPower() * (sourceEntityAttack / targetEntityDefense);


            } else if (move.getCategory() == MoveCategory.MAGIC) {

                int targetEntityMagic = gp.getEntityM().getEntityById(targetEntityId).getMagic();
                targetDamage = move.getPower() * (sourceEntityMagic / targetEntityMagic);

            } else {

                targetDamage = 0;
            }

            if (move.getCategory() != MoveCategory.SUPPORT) {

                if (gp.getCombatM().getGuardingEntities().contains(targetEntityId)) {                                   // Determine if the target entity is in a guarding state.

                    targetDamage /= 2;
                    gp.getCombatM().getGuardingEntities().remove(targetEntityId);
                }

                if (targetDamage <= 0) {

                    targetDamage = 1;                                                                                   // Guarantee that at least one life point is taken for non-support moves.
                }
            }

            if (gp.getEntityM().getEntityById(targetEntityId).getLife() - targetDamage < 0) {

                targetFinalLife = 0;
            } else {

                targetFinalLife = gp.getEntityM().getEntityById(targetEntityId).getLife() - targetDamage;
            }
            targetEntitiesFinalLife.put(targetEntityId, targetFinalLife);
        }
        return targetEntitiesFinalLife;
    }


    // GETTERS
    public MoveBase getMove() {
        return move;
    }

    public int getSourceEntityId() {
        return sourceEntityId;
    }

    public List<Integer> getTargetEntityIds() {
        return targetEntityIds;
    }
}
