package interaction;

import core.GamePanel;

/**
 * This abstract class defines base logic for post-conversation interactions.
 */
public abstract class InteractionConvBase {

    // FIELD
    protected final GamePanel gp;


    // CONSTRUCTOR
    /**
     * Constructs an InteractionConvBase instance.
     *
     * @param gp GamePanel instance
     */
    public InteractionConvBase(GamePanel gp) {
        this.gp = gp;
    }


    // METHOD
    /**
     * Runs post-conversation logic.
     */
    public abstract void run();
}
