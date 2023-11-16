package event;

import core.GamePanel;

/**
 * This abstract class defines base logic for post-conversation events.
 */
public abstract class EventConvBase {

    // FIELD
    protected final GamePanel gp;


    // CONSTRUCTOR
    /**
     * Constructs an EventConvBase instance.
     *
     * @param gp GamePanel instance
     */
    public EventConvBase(GamePanel gp) {
        this.gp = gp;
    }


    // METHOD
    /**
     * Runs post-conversation logic.
     */
    public abstract void run();
}
