package combat.implementation.action;

import combat.ActionBase;
import core.GamePanel;
import entity.EntityBase;
import event.support.PartySupport;

/**
 * This class defines a combat action (swap ally entities).
 */
public class Act_SwapAlly extends ActionBase {

    // FIELDS
    /**
     * ID of entity to swap.
     */
    private final int entityId1, entityId2;


    // CONSTRUCTOR
    public Act_SwapAlly(GamePanel gp, int entityId1, int entityId2) {
        super(gp);
        this.entityId1 = entityId1;
        this.entityId2 = entityId2;
    }


    // METHOD
    @Override
    public void run() {

        int swappeePosition = 0;
        int entityIndex = 0;

        for (EntityBase entity : gp.getParty().values()) {

            if (entity.getEntityId() == entityId2) {

                swappeePosition = entityIndex;
                break;
            } else {
                entityIndex++;
            }
        }
        gp.getPartyS().swapEntityInParty(entityId1, swappeePosition);
        gp.getCombatM().progressCombat();
    }
}
