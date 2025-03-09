package combat.implementation.action;

import combat.ActionBase;
import combat.MoveBase;
import combat.enumeration.MoveCategory;
import core.GamePanel;
import utility.LimitedArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
    private final ArrayList<Integer> targetEntityIds = new ArrayList<>();


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


    // TODO : Add possibility of move missing based on move accuracy.


    // METHODS
    @Override
    public void run() {

        if (move.getCategory() == MoveCategory.SUPPORT) {

            move.runEffects(sourceEntityId, targetEntityIds);
        } else {

            HashMap<Integer, Integer> targetEntitiesFinalLife = new HashMap<>();
            LimitedArrayList<Integer> evadedTargetEntityIds = new LimitedArrayList<>(targetEntityIds.size());

            for (int targetEntityId : targetEntityIds) {

                if (rollAccuracy()) {

                    targetEntitiesFinalLife.put(targetEntityId, calculateDamage(targetEntityId));                       // Calculate (but don't apply) final life value of the target entity.
                } else {

                    evadedTargetEntityIds.add(targetEntityId);
                }
            }

            if (evadedTargetEntityIds.size() > 0) {

                gp.getCombatM().addQueuedActionBack(
                        new Act_ReadMessage(gp, buildEvasionMessage(evadedTargetEntityIds), true, true));
            }
            gp.getCombatAnimationS().initiateStandardMoveAnimation(
                    sourceEntityId, targetEntitiesFinalLife, move, 0.4, 0.4);                                           // Play standard attack animation (fainting + move affects applied afterward).
        }
    }


    /**
     * Calculates damage to an entity targeted by this move.
     * Note that this method does not actually apply damage to the entity.
     *
     * @param targetEntityId ID of the entity to calculate damage for
     * @return final life points of the targeted entity after applying damage
     */
    private int calculateDamage(int targetEntityId) {

        int targetDamage;
        int targetFinalLife;

        if (move.getCategory() == MoveCategory.PHYSICAL) {

            int sourceEntityAttack = gp.getEntityM().getEntityById(sourceEntityId).getAttack();
            int targetEntityDefense = gp.getEntityM().getEntityById(targetEntityId).getDefense();
            targetDamage = move.getPower() * (sourceEntityAttack / targetEntityDefense);

        } else if (move.getCategory() == MoveCategory.MAGIC) {

            int sourceEntityMagic = gp.getEntityM().getEntityById(sourceEntityId).getMagic();
            int targetEntityMagic = gp.getEntityM().getEntityById(targetEntityId).getMagic();
            targetDamage = move.getPower() * (sourceEntityMagic / targetEntityMagic);

        } else {

            targetDamage = 0;
        }

        if (move.getCategory() != MoveCategory.SUPPORT) {

            if (gp.getCombatM().getGuardingEntities().contains(targetEntityId)) {                                       // Determine if the target entity is in a guarding state.

                targetDamage /= 2;
                gp.getCombatM().getGuardingEntities().remove(targetEntityId);
            }

            if (targetDamage <= 0) {

                targetDamage = 1;                                                                                       // Guarantee that at least one life point is taken for non-support moves.
            }
        }

        if (gp.getEntityM().getEntityById(targetEntityId).getLife() - targetDamage < 0) {

            targetFinalLife = 0;
        } else {

            targetFinalLife = gp.getEntityM().getEntityById(targetEntityId).getLife() - targetDamage;
        }
        return targetFinalLife;
    }


    /**
     * Builds entity move evasion message.
     *
     * @param evadedTargetEntitiesIds IDs of entities that evaded the move
     * @return message
     */
    private String buildEvasionMessage(LimitedArrayList<Integer> evadedTargetEntitiesIds) {

        String build = "";
        int i = 0;

        for (int entityId : evadedTargetEntitiesIds) {

            if (i == (evadedTargetEntitiesIds.size() - 1)) {

                if (i > 0) {

                    build += "and ";
                }
                build += gp.getEntityM().getEntityById(entityId).getName();
            } else {

                build += gp.getEntityM().getEntityById(entityId).getName();

                if (evadedTargetEntitiesIds.size() > 2) {

                    build += ", ";
                } else {

                    build += " ";
                }
            }
            i++;
        }
        return build + " evaded the attack!";
    }


    /**
     * Rolls to determine whether this move will land or not.
     *
     * @return whether this move will land (true) or not (false)
     */
    private boolean rollAccuracy() {

        Random random = new Random();
        int i = random.nextInt(100 + 1);                                                                                // Generate random number from one to one hundred (both inclusive).

        if (i <= move.getAccuracy()) {

            return true;
        } else {

            return false;
        }
    }


    // GETTERS
    public MoveBase getMove() {
        return move;
    }

    public int getSourceEntityId() {
        return sourceEntityId;
    }

    public ArrayList<Integer> getTargetEntityIds() {
        return targetEntityIds;
    }
}
