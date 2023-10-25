package submenu;

import core.GamePanel;
import utility.UtilityTool;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the sub-menu overlay that appears for the player to select an option.
 */
public class SubMenuHandler {

    // BASIC FIELDS
    private final GamePanel gp;

    /**
     * List of stored sub-menu options to display.
     * The default list is empty.
     */
    private final List<String> options = new ArrayList<>();

    /**
     * Unique ID of sub-menu currently being displayed.
     * To be clear, this ID is unique for each sub-menu in the game, as it determines what logic to run after an option
     * has been selected.
     * The default value is -1, indicating that there is no sub-menu currently being displayed.
     */
    private int subMenuId = -1;

    /**
     * Width of the sub-menu currently being displayed.
     * The default value is zero.
     */
    private int subMenuWidth;

    /**
     * Sub-menu's screen position.
     * This is the coordinate for of the top-left corner of the sub-menu window currently being displayed.
     * The default value is zero.
     */
    private int subMenuScreenX, subMenuScreenY;

    /**
     * Variable to store the selected index of the sub-menu currently being displayed.
     * The default value is zero.
     */
    private int indexSelected;


    // STATIC FIELDS
    /**
     * Standard width for a sub-menu window that only contains the options "Yes" and "No".
     */
    public static final int widthYesNo = 60;

    /**
     * Standard width for a sub-menu window that contains root combat options.
     */
    public static final int widthCombat1 = 100;


    // CONSTRUCTOR
    /**
     * Constructs a SubMenuHandler instance.
     *
     * @param gp GamePanel instance
     */
    public SubMenuHandler(GamePanel gp) {
        this.gp = gp;
    }


    /**
     * Generates a sub-menu.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param subMenuWidth width of the window (minimum value of 0)
     * @param subMenuScreenX x-coordinate of the left side of the window
     * @param subMenuScreenY y-coordinate on the top side of the window
     * @throws IllegalArgumentException if either an illegal number of options or an illegal width is passed as argument
     */
    public void generateSubMenu(List<String> options, int subMenuId, int subMenuWidth, int subMenuScreenX, int subMenuScreenY) {

        indexSelected = 0;                                                                                              // Ensures the default selected option is set to index zero.

        if ((options.size() >= 1) && (options.size() < 8) && (subMenuWidth >= 0)) {

            for (String item : options) {
                this.options.add(item);
            }
            this.subMenuId = subMenuId;
            this.subMenuScreenX = subMenuScreenX;
            this.subMenuScreenY = subMenuScreenY;
            this.subMenuWidth = subMenuWidth;
        } else {

            if ((options.size() < 1) || (options.size() >= 8)) {

                throw new IllegalArgumentException("Attempted to set a sub-menu with a number of options ("
                        + options.size()
                        + ") outside of bounds 1 - 8 (both inclusive)");
            } else if (subMenuWidth < 0) {

                throw new IllegalArgumentException("Attempted to set a sub-menu width less than zero");
            }
            gp.getInteractionM().handlePostSubMenu(subMenuId, indexSelected);
        }
    }


    /**
     * Resets SubMenuHandler back to its default state.
     * Intended to be called to clean up after a sub-menu option has been selected.
     */
    public void reset() {

        subMenuId = -1;
        subMenuWidth = 0;
        subMenuScreenX = 0;
        subMenuScreenY = 0;
        indexSelected = 0;
        options.clear();
    }


    // GETTERS
    public int getSubMenuId() {
        return subMenuId;
    }

    public int getSubMenuScreenX() {
        return subMenuScreenX;
    }

    public int getSubMenuScreenY() {
        return subMenuScreenY;
    }

    public int getSubMenuWidth() {
        return subMenuWidth;
    }

    public int getIndexSelected() {
        return indexSelected;
    }

    public List<String> getOptions() {
        return options;
    }


    // SETTER
    public void setIndexSelected(int indexSelected) {
        if ((indexSelected >= 0) && (indexSelected < options.size())) {
            this.indexSelected = indexSelected;
        }
    }
}
