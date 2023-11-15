package combat.implementation.action;

import combat.ActionBase;
import combat.SubMenuType;
import core.GamePanel;
import miscellaneous.GameState;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines an action (generate a sub-menu).
 */
public class Act_GenerateSubMenu extends ActionBase {

    // FIELDS
    private final SubMenuType type;
    private final List<String> options = new ArrayList<>();
    private final float subMenuScreenX;
    private final float subMenuScreenY;


    // CONSTRUCTORS
    public Act_GenerateSubMenu(GamePanel gp, SubMenuType type, List<String> options) {
        super(gp);
        this.type = type;
        for (String item : options) {
            this.options.add(item);
        }
        this.subMenuScreenX = 0.01f; // TODO : Adjust!
        this.subMenuScreenY = 0.01f; // TODO : Adjust!
    }


     public Act_GenerateSubMenu(GamePanel gp, SubMenuType type, List<String> options, float subMenuScreenX, float subMenuScreenY) {
        super(gp);
        this.type = type;
        for (String item : options) {
            this.options.add(item);
        }
        this.subMenuScreenX = subMenuScreenX;
        this.subMenuScreenY = subMenuScreenY;
    }


    // METHODS
    @Override
    public void run() {

        gp.getCombatM().setLastSubMenuType(type);
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
        gp.getSubMenuH().generateSubMenu(options, 1, subMenuScreenX, subMenuScreenY);
    }
}
