package combat.implementation.action;

import combat.ActionBase;
import core.GamePanel;

/**
 * This class defines a combat action (swap player side entities).
 * Note that any swapped entities in a guarding state will exit said state.
 */
public class Act_SwapPlayerSideEntity extends ActionBase {

    // FIELDS
    /**
     * ID of entity to swap.
     */
    private final int entityId1, entityId2;


    // CONSTRUCTOR
    public Act_SwapPlayerSideEntity(GamePanel gp, int entityId1, int entityId2) {
        super(gp);
        this.entityId1 = entityId1;
        this.entityId2 = entityId2;
    }


    // METHOD
    @Override
    public void run() {

        if (gp.getCombatM().getGuardingEntities().contains(entityId1)) {                                                // Remove entity swapped in/out from guarding state if applicable.

            gp.getCombatM().getGuardingEntities().remove(entityId1);
        }

        if (gp.getCombatM().getGuardingEntities().contains(entityId2)) {                                                // Remove entity swapped in/out from guarding state if applicable.

            gp.getCombatM().getGuardingEntities().remove(entityId2);
        }
        gp.getPartyS().swapEntityInParty(entityId1, entityId2, true);
        gp.getCombatM().progressCombat();
    }
}
