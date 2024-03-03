package combat.implementation.action;

import combat.ActionBase;
import combat.SubMenuType;
import core.GamePanel;
import miscellaneous.GameState;
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
    private final SubMenuType type;
    private final List<String> options = new ArrayList<>();
    private final float subMenuScreenX;
    private final float subMenuScreenY;
    private final boolean subMenuDefaultPosition;
    private final HashMap<Integer, Vector3f> colors = new HashMap<>();
    private final HashSet<Integer> disabledOptions = new HashSet<>();


    // CONSTRUCTORS
    public Act_GenerateSubMenu(GamePanel gp, SubMenuType type, List<String> options, float subMenuScreenX, float subMenuScreenY,
                               HashMap<Integer, Vector3f> colors, HashSet<Integer> disabledOptions) {
        this(gp, type, options, subMenuScreenX, subMenuScreenY, colors);
        for (int item : disabledOptions) {
            this.disabledOptions.add(item);
        }
    }


    public Act_GenerateSubMenu(GamePanel gp, SubMenuType type, List<String> options, float subMenuScreenX, float subMenuScreenY,
                               HashMap<Integer, Vector3f> colors) {
        this(gp, type, options, subMenuScreenX, subMenuScreenY);
        for (int key : colors.keySet()) {
            this.colors.put(key, colors.get(key));
        }
    }


    public Act_GenerateSubMenu(GamePanel gp, SubMenuType type, List<String> options, HashMap<Integer, Vector3f> colors,
                               HashSet<Integer> disabledOptions) {
        this(gp, type, options, colors);
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


    public Act_GenerateSubMenu(GamePanel gp, SubMenuType type, List<String> options, float subMenuScreenX, float subMenuScreenY,
                               HashSet<Integer> disabledOptions) {
        this(gp, type, options, subMenuScreenX, subMenuScreenY);
        for (int item : disabledOptions) {
            this.disabledOptions.add(item);
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


    public Act_GenerateSubMenu(GamePanel gp, SubMenuType type, List<String> options, HashSet<Integer> disabledOptions) {
        this(gp, type, options);
        for (int item : disabledOptions) {
            this.disabledOptions.add(item);
        }
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

        gp.getCombatM().addLastSubMenuType(type);
        gp.getCombatM().setLastActionSubmenu(true);
        displaySubMenu();
    }


    /**
     * Stages and initiates a sub-menu to appear during combat.
     * The sub-menu ID is set to 1 to indicate that this is a combat sub-menu.
     * The game state is set to sub-menu.
     */
    private void displaySubMenu() {

        gp.setGameState(GameState.SUB_MENU);
        if (subMenuDefaultPosition) {
            gp.getSubMenuH().generateSubMenu(options, 1, colors, disabledOptions);
        } else {
            gp.getSubMenuH().generateSubMenu(options, 1, subMenuScreenX, subMenuScreenY, colors, disabledOptions);
        }
    }
}
