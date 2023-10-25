package combat.implementation.action;

import combat.ActionBase;
import combat.SubMenuType;
import core.GamePanel;
import core.GameState;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines an action (generate a sub-menu).
 */
public class Act_GenerateSubMenu extends ActionBase {

    // FIELDS
    private final SubMenuType type;
    private final List<String> options = new ArrayList<>();
    private final int subMenuWidth;
    private final int subMenuScreenX;
    private final int subMenuScreenY;


    // CONSTRUCTORS
    public Act_GenerateSubMenu(GamePanel gp, SubMenuType type, List<String> options, int subMenuWidth) {
        super(gp);
        this.type = type;
        for (String item : options) {
            this.options.add(item);
        }
        this.subMenuWidth = subMenuWidth;
        this.subMenuScreenX = (gp.getScreenWidth() / gp.getScale()) - subMenuWidth - 30;
        this.subMenuScreenY = (gp.getScreenHeight() / gp.getScale()) - 100 - (22 * options.size()) - 6;
    }


     public Act_GenerateSubMenu(GamePanel gp, SubMenuType type, List<String> options, int subMenuWidth, int subMenuScreenX, int subMenuScreenY) {
        super(gp);
        this.type = type;
        for (String item : options) {
            this.options.add(item);
        }
        this.subMenuWidth = subMenuWidth;
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
        gp.getSubMenuH().generateSubMenu(options, 1, subMenuWidth, subMenuScreenX, subMenuScreenY);
    }
}
