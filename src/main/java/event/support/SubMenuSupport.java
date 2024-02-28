package event.support;

import core.GamePanel;
import miscellaneous.GameState;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class contains methods to facilitate sub-menus.
 */
public class SubMenuSupport {

    // FIELDS
    private final GamePanel gp;

    /**
     * List of stored sub-menu options to display.
     * This is used for a sub-menu to be displayed after a corresponding dialogue prompt has been read.
     * The default list is empty.
     */
    private final List<String> tempOptions = new ArrayList<>();

    /**
     * Stored sub-menu ID.
     * This is used for a sub-menu to be displayed after a corresponding dialogue prompt has been read.
     * The default value is zero.
     */
    private int tempSubMenuId;

    /**
     * Stored sub-menu screen position (top-left), normalized between 0 and 1.
     * This is used for a sub-menu to be displayed after a corresponding dialogue prompt has been read.
     * The default value is zero.
     */
    private float tempSubMenuScreenX, tempSubMenuScreenY;

    /**
     * Stored boolean indicating whether a sub-menu will be rendered at the default screen position or a custom screen
     * position.
     * This is used for a sub-menu to be displayed after a corresponding dialogue prompt has been read.
     * If this is marked as true, then the values of `tempSubMenuScreenX` and `tempSubMenuScreenY` will be ignored.
     * The default value is true.
     */
    private boolean tempSubMenuDefaultPosition = true;

    /**
     * Map of stored colors for each sub-menu option to display; option index is the key, color (r, g, b) is the value.
     * This is used for a sub-menu to be displayed after a corresponding dialogue prompt has been read.
     * The default map is empty.
     */
    private final HashMap<Integer, Vector3f> tempColors = new HashMap<>();


    // CONSTRUCTOR
    /**
     * Constructs a SubMenuSupport instance.
     *
     * @param gp GamePanel instance
     */
    public SubMenuSupport(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Generates the staged sub-menu to be displayed after a prompt has been read.
     */
    public void handlePostSubMenuPrompt() {

        // NOTE: This method is called as part of logic triggered by `displaySubMenuPrompt()` in InteractionManager.

        if (tempSubMenuDefaultPosition) {

            displaySubMenuBasic(tempOptions, tempSubMenuId, tempColors);                                                // Display the sub-menu now that the prompt has been read.
        } else {

            displaySubMenuBasic(tempOptions, tempSubMenuId, tempSubMenuScreenX, tempSubMenuScreenY, tempColors);        // Display the sub-menu now that the prompt has been read.
        }
        reset();                                                                                                        // Reset variables that stored values for the sub-menu back to their default values.
    }


    /**
     * Initiates a sub-menu to instantly appear with the passed options.
     * The game state is set to sub-menu.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param subMenuScreenX screen x-coordinate of the window (leftmost, normalized between 0 and 1)
     * @param subMenuScreenY screen y-coordinate of the window (topmost, normalized between 0 and 1)
     * @param colors map of colors for each sub-menu option; option index is the key, color (r, g, b) is the value
     */
    public void displaySubMenuBasic(List<String> options, int subMenuId, float subMenuScreenX, float subMenuScreenY,
                                    HashMap<Integer, Vector3f> colors) {

        gp.setGameState(GameState.SUB_MENU);
        gp.getSubMenuH().generateSubMenu(options, subMenuId, subMenuScreenX, subMenuScreenY, colors);
    }


    /**
     * Initiates a sub-menu to instantly appear with the passed options.
     * The game state is set to sub-menu.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param colors map of colors for each sub-menu option; option index is the key, color (r, g, b) is the value
     */
    public void displaySubMenuBasic(List<String> options, int subMenuId, HashMap<Integer, Vector3f> colors) {

        gp.setGameState(GameState.SUB_MENU);
        gp.getSubMenuH().generateSubMenu(options, subMenuId, colors);
    }


    /**
     * Initiates a sub-menu to instantly appear with the passed options.
     * The game state is set to sub-menu.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param subMenuScreenX screen x-coordinate of the window (leftmost, normalized between 0 and 1)
     * @param subMenuScreenY screen y-coordinate of the window (topmost, normalized between 0 and 1)
     */
    public void displaySubMenuBasic(List<String> options, int subMenuId, float subMenuScreenX, float subMenuScreenY) {

        gp.setGameState(GameState.SUB_MENU);
        gp.getSubMenuH().generateSubMenu(options, subMenuId, subMenuScreenX, subMenuScreenY);
    }


    /**
     * Initiates a sub-menu to instantly appear with the passed options.
     * The game state is set to sub-menu.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     */
    public void displaySubMenuBasic(List<String> options, int subMenuId) {

        gp.setGameState(GameState.SUB_MENU);
        gp.getSubMenuH().generateSubMenu(options, subMenuId);
    }


    /**
     * Initiates a sub-menu to appear after an accompanying prompt is read.
     * The game state is set to dialogue.
     *
     * @param prompt text to be displayed
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param subMenuScreenX screen x-coordinate of the window (leftmost, normalized between 0 and 1)
     * @param subMenuScreenY screen y-coordinate of the window (topmost, normalized between 0 and 1)
     * @param colors map of colors for each sub-menu option; option index is the key, color (r, g, b) is the value
     */
    public void displaySubMenuPrompt(String prompt, List<String> options, int subMenuId, int subMenuScreenX, int subMenuScreenY,
                                     HashMap<Integer, Vector3f> colors) {

        for (Integer key : colors.keySet()) {
            tempColors.put(key, colors.get(key));
        }
        displaySubMenuPrompt(prompt, options, subMenuId, subMenuScreenX, subMenuScreenY);
    }


    /**
     * Initiates a sub-menu to appear after an accompanying prompt is read.
     * The game state is set to dialogue.
     *
     * @param prompt text to be displayed
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param colors map of colors for each sub-menu option; option index is the key, color (r, g, b) is the value
     */
    public void displaySubMenuPrompt(String prompt, List<String> options, int subMenuId, HashMap<Integer, Vector3f> colors) {

        for (Integer key : colors.keySet()) {
            tempColors.put(key, colors.get(key));
        }
        displaySubMenuPrompt(prompt, options, subMenuId);
    }


    /**
     * Initiates a sub-menu to appear after an accompanying prompt is read.
     * The game state is set to dialogue.
     *
     * @param prompt text to be displayed
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param subMenuScreenX screen x-coordinate of the window (leftmost, normalized between 0 and 1)
     * @param subMenuScreenY screen y-coordinate of the window (topmost, normalized between 0 and 1)
     */
    public void displaySubMenuPrompt(String prompt, List<String> options, int subMenuId, int subMenuScreenX, int subMenuScreenY) {

        tempSubMenuScreenX = subMenuScreenX;
        tempSubMenuScreenY = subMenuScreenY;
        tempSubMenuDefaultPosition = false;
        displaySubMenuPrompt(prompt, options, subMenuId);
    }


    /**
     * Initiates a sub-menu to appear after an accompanying prompt is read.
     * The game state is set to dialogue.
     *
     * @param prompt text to be displayed
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     */
    public void displaySubMenuPrompt(String prompt, List<String> options, int subMenuId) {

        for (String item : options) {
            tempOptions.add(item);
        }
        tempSubMenuId = subMenuId;
        gp.setGameState(GameState.DIALOGUE);
        gp.getDialogueR().initiateSubMenuMessage(prompt);
    }


    /**
     * Resets SubMenuSupport back to its default state.
     * Intended to be called to clean up after a sub-menu prompt has finished.
     */
    private void reset() {

        tempOptions.clear();
        tempSubMenuId = 0;
        tempSubMenuScreenX = 0;
        tempSubMenuScreenY = 0;
        tempSubMenuDefaultPosition = true;
        tempColors.clear();
    }
}
