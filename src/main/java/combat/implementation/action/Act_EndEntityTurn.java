package combat.implementation.action;

import combat.ActionBase;
import core.GamePanel;

/**
 * This class defines an action (pop front entity off the turn order queue to proceed to the next entity's turn).
 */
public class Act_EndEntityTurn extends ActionBase {

    // CONSTRUCTOR
    public Act_EndEntityTurn(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

        gp.getCombatM().endEntityTurn();
    }
}
