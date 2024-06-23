package combat.implementation.action;

import combat.ActionBase;
import combat.enumeration.ExitCombatTransitionType;
import core.GamePanel;

/**
 * This class defines a combat action (exit combat).
 */
public class Act_ExitCombat extends ActionBase {

    // FIELD
    /**
     * Variable to store exit combat transition type to be performed.
     */
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
