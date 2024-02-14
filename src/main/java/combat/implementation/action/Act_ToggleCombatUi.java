package combat.implementation.action;

import combat.ActionBase;
import core.GamePanel;

/**
 * This class defines a combat action (toggle combat UI).
 */
public class Act_ToggleCombatUi extends ActionBase {

    // FIELD
    /**
     * Boolean indicating whether the combat UI will be toggled on (true) or off (false).
     */
    private final boolean combatUiVisible;


    // CONSTRUCTOR
    public Act_ToggleCombatUi(GamePanel gp, boolean combatUiVisible) {
        super(gp);
        this.combatUiVisible = combatUiVisible;
    }


    // METHOD
    @Override
    public void run() {

        gp.getCombatM().setCombatUiVisible(combatUiVisible);
        gp.getCombatM().progressCombat();
    }
}
