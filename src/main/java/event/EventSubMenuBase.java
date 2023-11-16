package event;

import core.GamePanel;

/**
 * This abstract class defines base logic for events attached to given sub-menus.
 */
public abstract class EventSubMenuBase {

    // FIELD
    protected final GamePanel gp;


    // CONSTRUCTOR
    /**
     * Constructs an EventSubMenuBase instance.
     *
     * @param gp GamePanel instance
     */
    public EventSubMenuBase(GamePanel gp) {
        this.gp = gp;
    }


    // METHOD
    /**
     * Runs sub-menu logic based on selected option.
     *
     * @param indexSelected list index of the selected option
     */
    public abstract void run(int indexSelected);
}
