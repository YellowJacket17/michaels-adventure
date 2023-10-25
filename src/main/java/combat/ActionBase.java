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
     */
    public abstract void run();
}
