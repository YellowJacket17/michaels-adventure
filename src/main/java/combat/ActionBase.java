package combat;

import core.GamePanel;

/**
 * This abstract class defines base logic for an action during a turn in combat.
 */
public abstract class ActionBase {

    // FIELD
    protected final GamePanel gp;


    // CONSTRUCTOR
    /**
     * Constructs an ActionBase instance.
     *
     * @param gp GamePanel instance
     */
    public ActionBase(GamePanel gp) {
        this.gp = gp;
    }


    // METHOD
    /**
     * Runs action logic.
     * Note that the `progressCombat()` method in CombatManager must be called somewhere through this method (typically
     * the end) to progress combat to the next action.
     */
    public abstract void run();
}
