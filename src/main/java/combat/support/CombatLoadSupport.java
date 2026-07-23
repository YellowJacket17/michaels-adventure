package combat.support;

import combat.implementation.load.Cbl_Scenario001;
import core.GamePanel;

/**
 * This class contains methods to facilitate executing custom logic for loading combat (both entering and exiting).
 */
public class CombatLoadSupport {

    /*
     * Note that any logic executed here will only be done so after all standard logic in the relevant loading phase has
     * been executed, unless otherwise specified.
     */

    // BASIC FIELD
    private final GamePanel gp;


    // COMBAT LOAD FIELDS
    private final Cbl_Scenario001 cbl_scenario001;


    // CONSTRUCTOR
    /**
     * Constructs a CombatLoadSupport instance.
     *
     * @param gp GamePanel instance
     */
    public CombatLoadSupport(GamePanel gp) {
        this.gp = gp;

        cbl_scenario001 = new Cbl_Scenario001(gp);
    }


    // METHODS
    /**
     * Executes any custom loading while the screen is faded to black when entering combat.
     * An example is setting the camera to a non-default position.
     *
     * @param combatId ID of the combat scenario
     */
    public void handleEnterCombatTransitionLoading(int combatId) {

        switch (combatId) {
            case 0:
                // Reserved value - do nothing.
                break;
            case 1:
                cbl_scenario001.handleEnterCombatTransitionLoading();
                break;
        }
    }


    /**
     * Executes any custom logic after the screen has faded from black when entering combat.
     * An example is adding an extra action to the list of queued combat actions.
     * Note that this occurs after the opening combat message is staged but before the combat UI and root sub-menu
     * are staged.
     *
     * @param combatId ID of the combat scenario
     */
    public void concludeEnterCombatTransition(int combatId) {

        switch (combatId) {
            case 0:
                // Reserved value - do nothing.
                break;
            case 1:
                cbl_scenario001.concludeEnterCombatTransition();
                break;
        }
    }


    /**
     * Executes any custom loading while the screen is faded to black when exiting combat.
     * An example is placing entities in specific non-default locations post-combat.
     *
     * @param combatId ID of the combat scenario
     * @param combatLost whether combat was lost by the player (true) or not (false)
     */
    public void handleExitCombatTransitionLoading(int combatId, boolean combatLost) {

        switch (combatId) {
            case 0:
                // Reserved value - do nothing.
                break;
            case 1:
                cbl_scenario001.handleExitCombatTransitionLoading(combatLost);
                break;
        }
    }


    /**
     * Executes any custom logic after the screen has faded from black when exiting combat.
     * An example is initiating a cutscene post-combat.
     *
     * @param combatId ID of the combat scenario
     * @param combatLost whether combat was lost by the player (true) or not (false)
     */
    public void concludeExitCombatTransition(int combatId, boolean combatLost) {

        switch (combatId) {
            case 0:
                // Reserved value - do nothing.
                break;
            case 1:
                cbl_scenario001.concludeExitCombatTransition(combatLost);
                break;
        }
    }
}
