package interaction.implementation.submenu;

import core.GamePanel;
import interaction.InteractionSubMenuBase;

/**
 * This class implements interaction logic for sub-menu with ID 1.
 * Note that a sub-menu ID of 1 is used for combat sub-menus.
 */
public class Int_SubMenu001 extends InteractionSubMenuBase {

    // CONSTRUCTOR
    public Int_SubMenu001(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run(int selectedIndex) {

        gp.getCombatM().setLastSelectedSubMenuOption(selectedIndex);
        gp.getInteractionM().cleanupSubmenu(3);
        gp.getCombatM().progressCombat();
    }
}
