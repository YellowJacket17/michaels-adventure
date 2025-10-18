package combat;

import core.GamePanel;

/**
 * This abstract class defines base custom logic for loading combat (both entering and exiting).
 */
public abstract class CombatLoadBase {

    // FIELD
    protected final GamePanel gp;


    // CONSTRUCTOR
    /**
     * Constructs an CombatLoadBase instance.
     *
     * @param gp GamePanel instance
     */
    public CombatLoadBase(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Executes any custom loading while the screen is faded to black when entering combat.
     */
    public abstract void handleEnterCombatTransitionLoading();


    /**
     * Executes any custom logic after the screen has faded from black when entering combat.
     */
    public abstract void concludeEnterCombatTransition();


    /**
     * Executes any custom loading while the screen is faded to black when exiting combat.
     */
    public abstract void handleExitCombatTransitionLoading();


    /**
     * Executes any custom logic after the screen has faded from black when exiting combat.
     */
    public abstract void concludeExitCombatTransition();
}
