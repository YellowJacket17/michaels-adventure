package event.implementation.submenu;

import core.GamePanel;
import event.EventSubMenuBase;

/**
 * This class implements post-selection event logic for sub-menu with ID 1.
 * Note that a sub-menu ID of 1 is used for combat sub-menus.
 */
public class Evt_SubMenu001 extends EventSubMenuBase {

    // CONSTRUCTOR
    public Evt_SubMenu001(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run(int selectedIndex) {

        gp.getCombatM().addLastSelectedSubMenuOption(selectedIndex);
        gp.getEventM().cleanupSubmenu(3);
        gp.getCombatM().progressCombat();
    }
}
