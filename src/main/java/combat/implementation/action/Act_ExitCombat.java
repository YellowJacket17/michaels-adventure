package combat.implementation.action;

import combat.ActionBase;
import combat.ExitCombatTransitionType;
import core.GamePanel;

/**
 * This class defines an action (exit combat).
 */
public class Act_ExitCombat extends ActionBase {

    // FIELD
    private final ExitCombatTransitionType type;


    // CONSTRUCTOR
    public Act_ExitCombat(GamePanel gp, ExitCombatTransitionType type) {
        super(gp);
        this.type = type;
    }


    // METHOD
    @Override
    public void run() {

        gp.getCombatM().exitCombat(type);
    }
}
