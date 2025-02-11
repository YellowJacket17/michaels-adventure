package submenu;

import core.GamePanel;
import core.enumeration.PrimaryGameState;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * This class handles the sub-menu overlay that appears for the player to select an option.
 */
public class SubMenuHandler {

    // FIELDS
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
     * Sub-menu's screen position, normalized from 0 to 1, both inclusive.
     * This is the coordinate of the top-left corner of the sub-menu window currently being displayed.
     * The default value is zero.
     */
    private float subMenuScreenX, subMenuScreenY;

    /**
     * Boolean indicating whether the sub-menu window will be rendered at the default screen position or a custom screen
     * position.
     * If this is marked as true, then the values of `subMenuScreenX` and `subMenuScreenY` will be ignored.
     * The default value is true.
     */
    private boolean subMenuDefaultPosition = true;

    /**
     * Variable to store the selected index of the sub-menu currently being displayed.
     * The default value is zero.
     */
    private int indexSelected;

    /**
     * Map of stored colors for each sub-menu option; option index is the key, color (r, g, b) is the value.
     * The default map is empty.
     */
    private final HashMap<Integer, Vector3f> colors = new HashMap<>();

    /**
     * Set to store the indices of any disabled sub-menu options.
     * The default set is empty.
     */
    private final HashSet<Integer> disabledOptions = new HashSet<>();


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
     * Generates and displays a sub-menu.
     * The primary game state is set to sub-menu.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param subMenuScreenX screen x-coordinate of the window (leftmost, normalized from 0 to 1, both inclusive)
     * @param subMenuScreenY screen y-coordinate of the window (topmost, normalized from 0 to 1, both inclusive)
     * @param colors map of colors for each sub-menu option; option index is the key, color (r, g, b) is the value
     * @param disabledOptions set of indices of disabled sub-menu options
     * @throws IllegalArgumentException if either an illegal number of options is passed as argument
     */
    public void generateSubMenu(List<String> options, int subMenuId, float subMenuScreenX, float subMenuScreenY,
                                HashMap<Integer, Vector3f> colors, HashSet<Integer> disabledOptions) {

        for (int item : disabledOptions) {
            this.disabledOptions.add(item);
        }
        generateSubMenu(options, subMenuId, subMenuScreenX, subMenuScreenY, colors);
    }


    /**
     * Generates and displays a sub-menu.
     * The primary game state is set to sub-menu.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param subMenuScreenX screen x-coordinate of the window (leftmost, normalized from 0 to 1, both inclusive)
     * @param subMenuScreenY screen y-coordinate of the window (topmost, normalized from 0 to 1, both inclusive)
     * @param colors map of colors for each sub-menu option; option index is the key, color (r, g, b) is the value
     * @throws IllegalArgumentException if either an illegal number of options is passed as argument
     */
    public void generateSubMenu(List<String> options, int subMenuId, float subMenuScreenX, float subMenuScreenY,
                                HashMap<Integer, Vector3f> colors) {

        for (int key : colors.keySet()) {
            this.colors.put(key, colors.get(key));
        }
        generateSubMenu(options, subMenuId, subMenuScreenX, subMenuScreenY);
    }


    /**
     * Generates and displays a sub-menu.
     * The primary game state is set to sub-menu.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param colors map of colors for each sub-menu option; option index is the key, color (r, g, b) is the value
     * @param disabledOptions set of indices of disabled sub-menu options
     * @throws IllegalArgumentException if either an illegal number of options is passed as argument
     */
    public void generateSubMenu(List<String> options, int subMenuId, HashMap<Integer, Vector3f> colors,
                                HashSet<Integer> disabledOptions) {

        for (int item : disabledOptions) {
            this.disabledOptions.add(item);
        }
        generateSubMenu(options, subMenuId, colors);
    }


    /**
     * Generates and displays a sub-menu.
     * The primary game state is set to sub-menu.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param colors map of colors for each sub-menu option; option index is the key, color (r, g, b) is the value
     * @throws IllegalArgumentException if either an illegal number of options is passed as argument
     */
    public void generateSubMenu(List<String> options, int subMenuId, HashMap<Integer, Vector3f> colors) {

        for (int key : colors.keySet()) {
            this.colors.put(key, colors.get(key));
        }
        generateSubMenu(options, subMenuId);
    }


    /**
     * Generates and displays a sub-menu.
     * The primary game state is set to sub-menu.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param subMenuScreenX screen x-coordinate of the window (leftmost, normalized from 0 to 1, both inclusive)
     * @param subMenuScreenY screen y-coordinate of the window (topmost, normalized from 0 to 1, both inclusive)
     * @param disabledOptions set of indices of disabled sub-menu options
     * @throws IllegalArgumentException if either an illegal number of options is passed as argument
     */
    public void generateSubMenu(List<String> options, int subMenuId, float subMenuScreenX, float subMenuScreenY,
                                HashSet<Integer> disabledOptions) {

       for (int item : disabledOptions) {
           this.disabledOptions.add(item);
       }
       generateSubMenu(options, subMenuId, subMenuScreenX, subMenuScreenY);
    }


    /**
     * Generates and displays a sub-menu.
     * The primary game state is set to sub-menu.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param subMenuScreenX screen x-coordinate of the window (leftmost, normalized from 0 to 1, both inclusive)
     * @param subMenuScreenY screen y-coordinate of the window (topmost, normalized from 0 to 1, both inclusive)
     * @throws IllegalArgumentException if either an illegal number of options is passed as argument
     */
    public void generateSubMenu(List<String> options, int subMenuId, float subMenuScreenX, float subMenuScreenY) {

        this.subMenuScreenX = subMenuScreenX;
        this.subMenuScreenY = subMenuScreenY;
        this.subMenuDefaultPosition = false;
        generateSubMenu(options, subMenuId);
    }


    /**
     * Generates and displays a sub-menu.
     * The primary game state is set to sub-menu.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param disabledOptions set of indices of disabled sub-menu options
     * @throws IllegalArgumentException if either an illegal number of options is passed as argument
     */
    public void generateSubMenu(List<String> options, int subMenuId, HashSet<Integer> disabledOptions) {

        for (int item : disabledOptions) {
            this.disabledOptions.add(item);
        }
        generateSubMenu(options, subMenuId);
    }


    /**
     * Generates and displays a sub-menu.
     * The primary game state is set to sub-menu.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @throws IllegalArgumentException if either an illegal number of options is passed as argument
     */
    public void generateSubMenu(List<String> options, int subMenuId) {

        gp.setPrimaryGameState(PrimaryGameState.SUB_MENU);
        indexSelected = 0;                                                                                              // Ensures the default selected option is set to index zero.

        if ((options.size() >= 1) && (options.size() <= 8)) {

            for (String item : options) {
                this.options.add(item);
            }
            this.subMenuId = subMenuId;
            gp.getEntityM().getPlayer().setInteractionCountdown(
                    gp.getEntityM().getPlayer().getStagedStandardInteractionCountdown());                               // Player must wait before interacting with the generated sub-menu else (helps avoid accidental selection). .
        } else {

            throw new IllegalArgumentException("Attempted to set a sub-menu with a number of options '"
                    + options.size()
                    + "' outside of bounds 1 - 8 (both inclusive)");
        }
    }


    /**
     * Resets SubMenuHandler back to its default state.
     * Intended to be called to clean up after a sub-menu option has been selected.
     */
    public void reset() {

        options.clear();
        subMenuId = -1;
        subMenuScreenX = 0;
        subMenuScreenY = 0;
        subMenuDefaultPosition = true;
        indexSelected = 0;
        colors.clear();
        disabledOptions.clear();
    }


    // GETTERS
    public List<String> getOptions() {
        return options;
    }

    public int getSubMenuId() {
        return subMenuId;
    }

    public float getSubMenuScreenX() {
        return subMenuScreenX;
    }

    public float getSubMenuScreenY() {
        return subMenuScreenY;
    }

    public boolean isSubMenuDefaultPosition() {
        return subMenuDefaultPosition;
    }

    public int getIndexSelected() {
        return indexSelected;
    }

    public HashMap<Integer, Vector3f> getColors() {
        return colors;
    }

    public HashSet<Integer> getDisabledOptions() {
        return disabledOptions;
    }


    // SETTER
    public void setIndexSelected(int indexSelected) {
        if ((indexSelected >= 0) && (indexSelected < options.size())) {
            this.indexSelected = indexSelected;
        }
    }
}
