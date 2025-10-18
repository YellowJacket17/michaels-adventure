package combat.implementation.action;

import combat.ActionBase;
import core.GamePanel;

/**
 * This class defines a combat action (exit combat).
 */
public class Act_ExitCombat extends ActionBase {

    // CONSTRUCTOR
    public Act_ExitCombat(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

        gp.getCombatM().exitCombat();
    }
}
