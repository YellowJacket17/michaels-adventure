package event.support;

import core.GamePanel;
import core.enumeration.PrimaryGameState;
import entity.EntityBase;
import org.joml.Vector3f;
import submenu.SubMenuHandler;
import utility.UtilityTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * This class contains methods to facilitate sub-menus.
 * The public methods in this class serve as primary endpoints to use when programming in-game events.
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
     * Stored sub-menu screen position (top-left), normalized from 0 to 1, both inclusive.
     * This is used for a sub-menu to be displayed after a corresponding dialogue prompt has been read.
     * The default value is zero.
     */
    private float tempSubMenuScreenX, tempSubMenuScreenY;

    /**
     * Stored boolean indicating whether a sub-menu will be rendered at the default screen position or a custom screen
     * position.
     * This is used for a sub-menu to be displayed after a corresponding dialogue prompt has been read.
     * If this is marked as true, then the values of 'tempSubMenuScreenX' and 'tempSubMenuScreenY' will be ignored.
     * The default value is true.
     */
    private boolean tempSubMenuDefaultPosition = true;

    /**
     * Map of stored colors for each sub-menu option to display; option index is the key, color (r, g, b) is the value.
     * This is used for a sub-menu to be displayed after a corresponding dialogue prompt has been read.
     * The default map is empty.
     */
    private final HashMap<Integer, Vector3f> tempColors = new HashMap<>();

    /**
     * Set to store the indices of any disabled sub-menu options.
     * This is used for a sub-menu to be displayed after a corresponding dialogue prompt has been read.
     * The default set is empty.
     */
    private final HashSet<Integer> tempDisabledOptions = new HashSet<>();


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

            displaySubMenuBasic(tempOptions, tempSubMenuId, tempColors, tempDisabledOptions);                           // Display the sub-menu now that the prompt has been read.
        } else {

            displaySubMenuBasic(tempOptions, tempSubMenuId, tempSubMenuScreenX, tempSubMenuScreenY, tempColors, tempDisabledOptions);
        }
        reset();                                                                                                        // Reset variables that stored values for the sub-menu back to their default values.
    }


    /**
     * Initiates a sub-menu to instantly appear with the passed options.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param subMenuScreenX screen x-coordinate of the window (leftmost, normalized from 0 to 1, both inclusive)
     * @param subMenuScreenY screen y-coordinate of the window (topmost, normalized from 0 to 1, both inclusive)
     * @param colors map of colors for each sub-menu option; option index is the key, color (r, g, b) is the value
     * @param disabledOptions set of indices of disabled sub-menu options
     */
    public void displaySubMenuBasic(List<String> options, int subMenuId, float subMenuScreenX, float subMenuScreenY,
                                    HashMap<Integer, Vector3f> colors, HashSet<Integer> disabledOptions) {

        gp.getSubMenuH().generateSubMenu(options, subMenuId, subMenuScreenX, subMenuScreenY, colors, disabledOptions);
    }


    /**
     * Initiates a sub-menu to instantly appear with the passed options.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param subMenuScreenX screen x-coordinate of the window (leftmost, normalized from 0 to 1, both inclusive)
     * @param subMenuScreenY screen y-coordinate of the window (topmost, normalized from 0 to 1, both inclusive)
     * @param colors map of colors for each sub-menu option; option index is the key, color (r, g, b) is the value
     */
    public void displaySubMenuBasic(List<String> options, int subMenuId, float subMenuScreenX, float subMenuScreenY,
                                    HashMap<Integer, Vector3f> colors) {

        gp.getSubMenuH().generateSubMenu(options, subMenuId, subMenuScreenX, subMenuScreenY, colors);
    }


    /**
     * Initiates a sub-menu to instantly appear with the passed options.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param colors map of colors for each sub-menu option; option index is the key, color (r, g, b) is the value
     * @param disabledOptions set of indices of disabled sub-menu options
     */
    public void displaySubMenuBasic(List<String> options, int subMenuId, HashMap<Integer, Vector3f> colors,
                                    HashSet<Integer> disabledOptions) {

        gp.getSubMenuH().generateSubMenu(options, subMenuId, colors, disabledOptions);
    }


    /**
     * Initiates a sub-menu to instantly appear with the passed options.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param colors map of colors for each sub-menu option; option index is the key, color (r, g, b) is the value
     */
    public void displaySubMenuBasic(List<String> options, int subMenuId, HashMap<Integer, Vector3f> colors) {

        gp.getSubMenuH().generateSubMenu(options, subMenuId, colors);
    }


    /**
     * Initiates a sub-menu to instantly appear with the passed options.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param subMenuScreenX screen x-coordinate of the window (leftmost, normalized from 0 to 1, both inclusive)
     * @param subMenuScreenY screen y-coordinate of the window (topmost, normalized from 0 to 1, both inclusive)
     * @param disabledOptions set of indices of disabled sub-menu options
     */
    public void displaySubMenuBasic(List<String> options, int subMenuId, float subMenuScreenX, float subMenuScreenY,
                                    HashSet<Integer> disabledOptions) {

        gp.getSubMenuH().generateSubMenu(options, subMenuId, subMenuScreenX, subMenuScreenY, disabledOptions);
    }


    /**
     * Initiates a sub-menu to instantly appear with the passed options.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param subMenuScreenX screen x-coordinate of the window (leftmost, normalized from 0 to 1, both inclusive)
     * @param subMenuScreenY screen y-coordinate of the window (topmost, normalized from 0 to 1, both inclusive)
     */
    public void displaySubMenuBasic(List<String> options, int subMenuId, float subMenuScreenX, float subMenuScreenY) {

        gp.getSubMenuH().generateSubMenu(options, subMenuId, subMenuScreenX, subMenuScreenY);
    }


    /**
     * Initiates a sub-menu to instantly appear with the passed options.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param disabledOptions set of indices of disabled sub-menu options
     */
    public void displaySubMenuBasic(List<String> options, int subMenuId, HashSet<Integer> disabledOptions) {

        gp.getSubMenuH().generateSubMenu(options, subMenuId, disabledOptions);
    }


    /**
     * Initiates a sub-menu to instantly appear with the passed options.
     *
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     */
    public void displaySubMenuBasic(List<String> options, int subMenuId) {

        gp.getSubMenuH().generateSubMenu(options, subMenuId);
    }


    /**
     * Initiates a sub-menu to appear after an accompanying prompt is read.
     * The primary game state is set to dialogue.
     *
     * @param prompt text to be displayed
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param charByChar whether visible text will be printed character by character (true) or all at once (false)
     * @param subMenuScreenX screen x-coordinate of the window (leftmost, normalized from 0 to 1, both inclusive)
     * @param subMenuScreenY screen y-coordinate of the window (topmost, normalized from 0 to 1, both inclusive)
     * @param colors map of colors for each sub-menu option; option index is the key, color (r, g, b) is the value
     * @param disabledOptions set of indices of disabled sub-menu options
     */
    public void displaySubMenuPrompt(String prompt, List<String> options, int subMenuId, boolean charByChar,
                                     int subMenuScreenX, int subMenuScreenY, HashMap<Integer, Vector3f> colors,
                                     HashSet<Integer> disabledOptions) {

        for (int item : disabledOptions) {
            this.tempDisabledOptions.add(item);
        }
        displaySubMenuPrompt(prompt, options, subMenuId, charByChar, subMenuScreenX, subMenuScreenY, colors);
    }


    /**
     * Initiates a sub-menu to appear after an accompanying prompt is read.
     * The primary game state is set to dialogue.
     *
     * @param prompt text to be displayed
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param charByChar whether visible text will be printed character by character (true) or all at once (false)
     * @param subMenuScreenX screen x-coordinate of the window (leftmost, normalized from 0 to 1, both inclusive)
     * @param subMenuScreenY screen y-coordinate of the window (topmost, normalized from 0 to 1, both inclusive)
     * @param colors map of colors for each sub-menu option; option index is the key, color (r, g, b) is the value
     */
    public void displaySubMenuPrompt(String prompt, List<String> options, int subMenuId, boolean charByChar,
                                     int subMenuScreenX, int subMenuScreenY, HashMap<Integer, Vector3f> colors) {

        for (Integer key : colors.keySet()) {
            tempColors.put(key, colors.get(key));
        }
        displaySubMenuPrompt(prompt, options, subMenuId, charByChar, subMenuScreenX, subMenuScreenY);
    }


    /**
     * Initiates a sub-menu to appear after an accompanying prompt is read.
     * The primary game state is set to dialogue.
     *
     * @param prompt text to be displayed
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param charByChar whether visible text will be printed character by character (true) or all at once (false)
     * @param colors map of colors for each sub-menu option; option index is the key, color (r, g, b) is the value
     * @param disabledOptions set of indices of disabled sub-menu options
     */
    public void displaySubMenuPrompt(String prompt, List<String> options, int subMenuId, boolean charByChar,
                                     HashMap<Integer, Vector3f> colors, HashSet<Integer> disabledOptions) {

        for (int item : disabledOptions) {
            this.tempDisabledOptions.add(item);
        }
        displaySubMenuPrompt(prompt, options, subMenuId, charByChar, colors);
    }


    /**
     * Initiates a sub-menu to appear after an accompanying prompt is read.
     * The primary game state is set to dialogue.
     *
     * @param prompt text to be displayed
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param charByChar whether visible text will be printed character by character (true) or all at once (false)
     * @param colors map of colors for each sub-menu option; option index is the key, color (r, g, b) is the value
     */
    public void displaySubMenuPrompt(String prompt, List<String> options, int subMenuId, boolean charByChar,
                                     HashMap<Integer, Vector3f> colors) {

        for (Integer key : colors.keySet()) {
            tempColors.put(key, colors.get(key));
        }
        displaySubMenuPrompt(prompt, options, subMenuId, charByChar);
    }


    /**
     * Initiates a sub-menu to appear after an accompanying prompt is read.
     * The primary game state is set to dialogue.
     *
     * @param prompt text to be displayed
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param charByChar whether visible text will be printed character by character (true) or all at once (false)
     * @param subMenuScreenX screen x-coordinate of the window (leftmost, normalized from 0 to 1, both inclusive)
     * @param subMenuScreenY screen y-coordinate of the window (topmost, normalized from 0 to 1, both inclusive)
     * @param disabledOptions set of indices of disabled sub-menu options
     */
    public void displaySubMenuPrompt(String prompt, List<String> options, int subMenuId, boolean charByChar,
                                     int subMenuScreenX, int subMenuScreenY, HashSet<Integer> disabledOptions) {

        for (int item : disabledOptions) {
            this.tempDisabledOptions.add(item);
        }
        displaySubMenuPrompt(prompt, options, subMenuId, charByChar, subMenuScreenX, subMenuScreenY);
    }


    /**
     * Initiates a sub-menu to appear after an accompanying prompt is read.
     * The primary game state is set to dialogue.
     *
     * @param prompt text to be displayed
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param charByChar whether visible text will be printed character by character (true) or all at once (false)
     * @param subMenuScreenX screen x-coordinate of the window (leftmost, normalized from 0 to 1, both inclusive)
     * @param subMenuScreenY screen y-coordinate of the window (topmost, normalized from 0 to 1, both inclusive)
     */
    public void displaySubMenuPrompt(String prompt, List<String> options, int subMenuId, boolean charByChar,
                                     int subMenuScreenX, int subMenuScreenY) {

        tempSubMenuScreenX = subMenuScreenX;
        tempSubMenuScreenY = subMenuScreenY;
        tempSubMenuDefaultPosition = false;
        displaySubMenuPrompt(prompt, options, subMenuId, charByChar);
    }


    /**
     * Initiates a sub-menu to appear after an accompanying prompt is read.
     * The primary game state is set to dialogue.
     *
     * @param prompt text to be displayed
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param charByChar whether visible text will be printed character by character (true) or all at once (false)
     * @param disabledOptions set of indices of disabled sub-menu options
     */
    public void displaySubMenuPrompt(String prompt, List<String> options, int subMenuId, boolean charByChar,
                                     HashSet<Integer> disabledOptions) {

        for (int item : disabledOptions) {
            this.tempDisabledOptions.add(item);
        }
        displaySubMenuPrompt(prompt, options, subMenuId, charByChar);
    }


    /**
     * Initiates a sub-menu to appear after an accompanying prompt is read.
     *
     * @param prompt text to be displayed
     * @param options list of options to be displayed in the sub-menu (minimum size of 1, maximum of 8)
     * @param subMenuId ID of the sub-menu; this is used to determine what logic should be triggered upon selecting an option
     * @param charByChar whether visible text will be printed character by character (true) or all at once (false)
     */
    public void displaySubMenuPrompt(String prompt, List<String> options, int subMenuId, boolean charByChar) {

        for (String item : options) {
            tempOptions.add(item);
        }
        tempSubMenuId = subMenuId;
        gp.getDialogueR().initiateSubMenuMessage(prompt, charByChar);
    }


    /**
     * Generates a sub-menu prompt to reset the camera (snap) back to the tracked entity.
     */
    public void generateResetCameraSnapSubMenuPrompt() {

        List<String> options = List.of("Yes", "No");                                                                    // Immutable list.
        String prompt = "Reset camera back to tracked entity?";
        gp.getSubMenuS().displaySubMenuPrompt(prompt, options, 2, false);
    }


    /**
     * Generates a sub-menu prompt to swap party members in the party menu screen.
     * Note that the party menu screen must be active and a non-player entity must be selected.
     */
    public void generatePartySwapSubMenuPrompt() {

        if ((gp.getPrimaryGameState() == PrimaryGameState.PARTY_MENU)
                && (gp.getUi().getSelectedPartyMenuEntity() != gp.getEntityM().getPlayer().getEntityId())) {

            List<String> options = new ArrayList<>();
            EntityBase primaryEntity = gp.getEntityM().getEntityById(gp.getUi().getSelectedPartyMenuEntity());

            for (EntityBase candidateEntity : gp.getEntityM().getParty().values()) {

                if (candidateEntity.getEntityId() != primaryEntity.getEntityId()) {

                    options.add(candidateEntity.getName());
                }
            }
            options.add("Cancel");
            HashMap<Integer, Vector3f> colors = new HashMap<>();
            colors.put(options.size() - 1, SubMenuHandler.BACK_OPTION_COLOR);
            String prompt = "Swap " + primaryEntity.getName() + " with who?";
            gp.getSubMenuS().displaySubMenuPrompt(prompt, options, 3, false, colors);
        } else {

            UtilityTool.logWarning("Attempted to illegally generate a party menu sub-menu prompt to swap entities.");
        }
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
        tempDisabledOptions.clear();
        tempDisabledOptions.clear();
    }
}
