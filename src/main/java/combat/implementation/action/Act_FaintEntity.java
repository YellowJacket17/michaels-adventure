package combat.implementation.action;

import combat.ActionBase;
import core.GamePanel;

import java.util.ArrayList;

/**
 * This class defines a combat action (set an entity to a fainted status).
 * Note that any swapped entities in a guarding state will exit said state.
 */
public class Act_FaintEntity extends ActionBase {

    // FIELD
    private final ArrayList<Integer> entityIds = new ArrayList<>();


    // CONSTRUCTORS
    public Act_FaintEntity(GamePanel gp, int entityId) {
        super(gp);
        this.entityIds.add(entityId);
    }


    public Act_FaintEntity(GamePanel gp, ArrayList<Integer> entityIds) {
        super(gp);
        for (int entityId : entityIds) {
            this.entityIds.add(entityId);
        }
    }


    // METHOD
    @Override
    public void run() {

        for (int entityId : entityIds) {

            if (gp.getCombatM().getGuardingEntities().contains(entityId)) {                                             // Remove fainting entity from guarding state if applicable.

                gp.getCombatM().getGuardingEntities().remove(entityId);
            }
            gp.getEntityM().getEntityById(entityId).resetSecondaryAttributes();                                         // Reset attack, defense, magic, and agility attributes if applicable.

            if (gp.getCombatM().getTargetLockEntityId() == entityId) {

                gp.getCombatM().resetTargetLockEntity();
            }
        }
        gp.getCombatAnimationS().initiateStandardFaintAnimation(entityIds, 0, 0.1);

        // NOTE: The `progressCombat()` method in CombatManager to hand off control to the next queued action will
        // automatically be called once this animation in the `updateStandardFaintAnimation()` method in
        // CombatAnimationSupport is complete, hence why it is not called here.
    }
}
