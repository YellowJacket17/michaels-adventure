package combat.implementation.action;

import combat.ActionBase;
import core.GamePanel;

import java.util.ArrayList;

/**
 * This class defines a combat action (revive entity).
 */
public class Act_ReviveEntity extends ActionBase {

    // FIELD
    /**
     * IDs of entities to be revived.
     */
    private final ArrayList<Integer> entityIds = new ArrayList<>();


    // CONSTRUCTOR
    public Act_ReviveEntity(GamePanel gp, ArrayList<Integer> entityIds) {
        super(gp);
        for (int entityId : entityIds) {
            this.entityIds.add(entityId);
        }
    }


    // METHOD
    @Override
    public void run() {

        gp.getCombatAnimationS().initiateStandardReviveAnimation(entityIds, 0.4, 0.4);
    }
}
