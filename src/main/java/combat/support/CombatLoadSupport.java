package combat.support;

import core.GamePanel;

/**
 * This class contains methods to facilitate executing custom logic for loading combat (both entering and exiting).
 */
public class CombatLoadSupport {

    /*
     * Note that any logic executed here will only be done so after all standard logic in the relevant loading phase has
     * been executed.
     * An
     */

    // BASIC FIELD
    private final GamePanel gp;


    // COMBAT LOAD FIELDS
    // None here... yet. Cbl_Scenario001


    // CONSTRUCTOR
    /**
     * Constructs a CombatLoadSupport instance.
     *
     * @param gp GamePanel instance
     */
    public CombatLoadSupport(GamePanel gp) {
        this.gp = gp;
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
        }
    }


    /**
     * Executes any custom logic after the screen has faded from black when entering combat.
     * An example is adding an extra action to the list of queued combat actions.
     *
     * @param combatId ID of the combat scenario
     */
    public void concludeEnterCombatTransition(int combatId) {

        switch (combatId) {
            case 0:
                // Reserved value - do nothing.
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
        }
    }
}
