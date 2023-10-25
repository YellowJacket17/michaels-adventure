package interaction;

import core.GamePanel;

/**
 * This abstract class defines base logic for interactions on given sub-menus.
 */
public abstract class InteractionSubMenuBase {

    // FIELD
    protected final GamePanel gp;


    // CONSTRUCTOR
    /**
     * Constructs an InteractionSubMenuBase instance.
     *
     * @param gp GamePanel instance
     */
    public InteractionSubMenuBase(GamePanel gp) {
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
