package combat.implementation.action;

import combat.ActionBase;
import combat.MoveBase;
import combat.enumeration.MoveCategory;
import core.GamePanel;

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
    private final List<Integer> targetEntityIds;


    // CONSTRUCTORS
    public Act_UseMove(GamePanel gp, MoveBase move, int sourceEntityId, int targetEntityId) {
        super(gp);
        this.move = move;
        this.sourceEntityId = sourceEntityId;
        this.targetEntityIds = List.of(targetEntityId);                                                                 // Immutable list.
    }


    public Act_UseMove(GamePanel gp, MoveBase move, int sourceEntityId, int targetEntityId1, int targetEntityId2) {
        super(gp);
        this.move = move;
        this.sourceEntityId = sourceEntityId;
        this.targetEntityIds = List.of(targetEntityId1, targetEntityId2);                                               // Immutable list.
    }


    public Act_UseMove(GamePanel gp, MoveBase move, int sourceEntityId, int targetEntityId1, int targetEntityId2,
                       int targetEntityId3) {
        super(gp);
        this.move = move;
        this.sourceEntityId = sourceEntityId;
        this.targetEntityIds = List.of(targetEntityId1, targetEntityId2, targetEntityId3);                              // Immutable list.
    }


    public Act_UseMove(GamePanel gp, MoveBase move, int sourceEntityId, int targetEntityId1, int targetEntityId2,
                       int targetEntityId3, int targetEntityId4) {
        super(gp);
        this.move = move;
        this.sourceEntityId = sourceEntityId;
        this.targetEntityIds = List.of(targetEntityId1, targetEntityId2, targetEntityId3, targetEntityId4);             // Immutable list.
    }


    public Act_UseMove(GamePanel gp, MoveBase move, int sourceEntityId, int targetEntityId1, int targetEntityId2,
                       int targetEntityId3, int targetEntityId4, int targetEntityId5) {
        super(gp);
        this.move = move;
        this.sourceEntityId = sourceEntityId;
        this.targetEntityIds = List.of(targetEntityId1, targetEntityId2, targetEntityId3, targetEntityId4,
                targetEntityId5);                                                                                       // Immutable list.
    }


    public Act_UseMove(GamePanel gp, MoveBase move, int sourceEntityId, int targetEntityId1, int targetEntityId2,
                       int targetEntityId3, int targetEntityId4, int targetEntityId5, int targetEntityId6) {
        super(gp);
        this.move = move;
        this.sourceEntityId = sourceEntityId;
        this.targetEntityIds = List.of(targetEntityId1, targetEntityId2, targetEntityId3, targetEntityId4,
                targetEntityId5, targetEntityId6);                                                                      // Immutable list.
    }


    // METHOD
    @Override
    public void run() {

        HashMap<Integer, Integer> targetEntitiesFinalLife = calculateDamage();                                          // Calculate (but don't apply) final life values of each target entity.
        gp.getCombatAnimationS().initiateStandardAttackAnimation(sourceEntityId, targetEntitiesFinalLife, move);        // Play standard attack animation (fainting + move affects applied afterward).
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
        int targetDamage = 0;
        boolean targetGuarding;

        for (int targetEntityId : targetEntityIds) {                                                                    // Calculate and apply damage dealt to each target entity.

            if (gp.getCombatM().getGuardingEntities().contains(targetEntityId)) {                                       // Determine if the target entity is in a guarding state.

                targetGuarding = true;
                gp.getCombatM().getGuardingEntities().remove(targetEntityId);
                String message = gp.getEntityM()
                        .getEntityById(targetEntityId).getName() + "'s defensive stance was broken!";
                gp.getCombatM().addQueuedActionBack(new Act_ReadMessage(gp, message, true, true));
            } else {

                targetGuarding = false;
            }

            if (move.getCategory() == MoveCategory.PHYSICAL) {

                int targetEntityDefense = gp.getEntityM().getEntityById(targetEntityId).getDefense();
                targetDamage = move.getPower() * (sourceEntityAttack / targetEntityDefense);
                if (targetGuarding) {targetDamage /= 2;}
                targetEntitiesFinalLife.put(
                        targetEntityId,
                        gp.getEntityM().getEntityById(targetEntityId).getLife() - targetDamage);
            } else if (move.getCategory() == MoveCategory.MAGIC) {

                int targetEntityMagic = gp.getEntityM().getEntityById(targetEntityId).getMagic();
                targetDamage = move.getPower() * (sourceEntityMagic / targetEntityMagic);
                if (targetGuarding) {targetDamage /= 2;}
                targetEntitiesFinalLife.put(
                        targetEntityId,
                        gp.getEntityM().getEntityById(targetEntityId).getLife() - targetDamage);
            }
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
