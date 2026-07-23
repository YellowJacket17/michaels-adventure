package event;

import core.GamePanel;

/**
 * This abstract class defines base logic for events attached to given tutorials.
 */
public abstract class EventTutorialBase {

    // FIELD
    protected final GamePanel gp;


    // CONSTRUCTOR
    /**
     * Constructs an EventTutorialBase instance.
     *
     * @param gp GamePanel instance
     */
    public EventTutorialBase(GamePanel gp) {
        this.gp = gp;
    }


    // METHOD
    /**
     * Runs post-tutorial logic based on the displayed tutorial.
     */
    public abstract void run();
}
