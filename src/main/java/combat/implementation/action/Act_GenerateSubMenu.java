package combat.implementation.action;

import combat.ActionBase;
import combat.SubMenuMemory;
import combat.enumeration.SubMenuType;
import core.GamePanel;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * This class defines a combat action (generate a sub-menu).
 */
public class Act_GenerateSubMenu extends ActionBase {

    // FIELDS
    /**
     * Sub-menu type.
     */
    private final SubMenuType type;

    /**
     * List of stored sub-menu options to display. The default list is empty
     */
    private final List<String> options = new ArrayList<>();

    /**
     * Sub-menu's screen position, normalized from 0 to 1, both inclusive.
     * This is the coordinate of the top-left corner of the sub-menu window currently being displayed.
     * The default value is zero.
     */
    private final float subMenuScreenX, subMenuScreenY;

    /**
     * Boolean indicating whether the sub-menu window will be rendered at the default screen position or a custom screen
     * position.
     * If this is marked as true, then the values of `subMenuScreenX` and `subMenuScreenY` will be ignored.
     * The default value is true.
     */
    private final boolean subMenuDefaultPosition;

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

    /**
     * Map to store descriptions for each sub-menu option; option index is the key, description is the value.
     * The default map is empty.
     */
    private final HashMap<Integer, String> descriptions = new HashMap<>();


    // CONSTRUCTORS
    public Act_GenerateSubMenu(GamePanel gp, SubMenuType type, List<String> options, float subMenuScreenX, float subMenuScreenY,
                               HashMap<Integer, Vector3f> colors, HashSet<Integer> disabledOptions, HashMap<Integer, String> descriptions) {
        this(gp, type, options, subMenuScreenX, subMenuScreenY);
        for (int key : colors.keySet()) {
            this.colors.put(key, colors.get(key));
        }
        for (int item : disabledOptions) {
            this.disabledOptions.add(item);
        }
        for (int key : descriptions.keySet()) {
            this.descriptions.put(key, descriptions.get(key));
        }
    }


    public Act_GenerateSubMenu(GamePanel gp, SubMenuType type, List<String> options, HashMap<Integer, Vector3f> colors,
                               HashSet<Integer> disabledOptions, HashMap<Integer, String> descriptions) {
        this(gp, type, options, colors, disabledOptions);
        for (int key : descriptions.keySet()) {
            this.descriptions.put(key, descriptions.get(key));
        }
    }


    public Act_GenerateSubMenu(GamePanel gp, SubMenuType type, List<String> options, HashSet<Integer> disabledOptions,
                               HashMap<Integer, String> descriptions) {
        this(gp, type, options, disabledOptions);
        for (int key : descriptions.keySet()) {
            this.descriptions.put(key, descriptions.get(key));
        }
    }


    public Act_GenerateSubMenu(GamePanel gp, SubMenuType type, List<String> options, HashMap<Integer, Vector3f> colors,
                               HashMap<Integer, String> descriptions) {
        this(gp, type, options, colors);
        for (int key : descriptions.keySet()) {
            this.descriptions.put(key, descriptions.get(key));
        }
    }


    public Act_GenerateSubMenu(GamePanel gp, SubMenuType type, List<String> options, HashMap<Integer, Vector3f> colors,
                               HashSet<Integer> disabledOptions) {
        this(gp, type, options, colors);
        for (int item : disabledOptions) {
            this.disabledOptions.add(item);
        }
    }


    public Act_GenerateSubMenu(GamePanel gp, SubMenuType type, List<String> options, HashSet<Integer> disabledOptions) {
        this(gp, type, options);
        for (int item : disabledOptions) {
            this.disabledOptions.add(item);
        }
    }


    public Act_GenerateSubMenu(GamePanel gp, SubMenuType type, List<String> options, HashMap<Integer, Vector3f> colors) {
        this(gp, type, options);
        for (int key : colors.keySet()) {
            this.colors.put(key, colors.get(key));
        }
    }


    public Act_GenerateSubMenu(GamePanel gp, SubMenuType type, List<String> options, float subMenuScreenX, float subMenuScreenY) {
        super(gp);
        this.type = type;
        for (String item : options) {
            this.options.add(item);
        }
        this.subMenuScreenX = subMenuScreenX;
        this.subMenuScreenY = subMenuScreenY;
        this.subMenuDefaultPosition = false;
    }


    public Act_GenerateSubMenu(GamePanel gp, SubMenuType type, List<String> options) {
        super(gp);
        this.type = type;
        for (String item : options) {
            this.options.add(item);
        }
        this.subMenuScreenX = 0;
        this.subMenuScreenY = 0;
        this.subMenuDefaultPosition = true;
    }


    // METHODS
    @Override
    public void run() {

        SubMenuMemory subMenuMemory = new SubMenuMemory(options, type, descriptions);
        gp.getCombatM().addSubMenuMemory(subMenuMemory);
        gp.getCombatM().setLastActionSubmenu(true);
        displaySubMenu();
    }


    /**
     * Stages and initiates a sub-menu to appear during combat.
     * The sub-menu ID is set to 1 to indicate that this is a combat sub-menu.
     */
    private void displaySubMenu() {

        if (subMenuDefaultPosition) {

            gp.getSubMenuH().generateSubMenu(options, 1, colors, disabledOptions);
        } else {

            gp.getSubMenuH().generateSubMenu(options, 1, subMenuScreenX, subMenuScreenY, colors, disabledOptions);
        }
    }
}
