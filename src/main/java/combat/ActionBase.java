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
     * Note that the 'progressCombat()' method in CombatManager must be called somewhere through this action's logic to
     * hand off control to the next queued action, thereby progressing combat.
     * Tip: if searching for code that progresses combat to the next action (i.e., code that ends an action), search
     * files for uses of the 'progressCombat()' method.
     */
    public abstract void run();
}
