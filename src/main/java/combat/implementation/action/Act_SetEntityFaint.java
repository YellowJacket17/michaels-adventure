package combat.implementation.action;

import combat.ActionBase;
import core.GamePanel;

import java.util.ArrayList;

/**
 * This class defines a combat action (set an entity to a fainted status).
 */
public class Act_SetEntityFaint extends ActionBase {

    // FIELD
    private final ArrayList<Integer> entityIds = new ArrayList<>();


    // CONSTRUCTORS
    public Act_SetEntityFaint(GamePanel gp, int entityId) {
        super(gp);
        this.entityIds.add(entityId);
    }


    public Act_SetEntityFaint(GamePanel gp, ArrayList<Integer> entityIds) {
        super(gp);
        for (int entityId : entityIds) {
            this.entityIds.add(entityId);
        }
    }


    // METHOD
    @Override
    public void run() {

        gp.getCombatAnimationS().initiateStandardFaintAnimation(entityIds);
    }
}
