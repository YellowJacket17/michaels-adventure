package event.implementation.submenu;

import core.GamePanel;
import event.EventSubMenuBase;

/**
 * This class implements post-selection event logic for sub-menu with ID 4.
 * Note that a sub-menu ID of 4 is used for selecting whether to view the opening cutscene or not.
 */
public class Evt_SubMenu004 extends EventSubMenuBase {

    // CONSTRUCTOR
    public Evt_SubMenu004(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run(int selectedIndex) {

        if (selectedIndex == 0) {

            gp.getCutsceneM().initiateCutscene(0);
        } else {

            gp.getCutsceneM().initiateCutscene(1);
        }
        gp.getEventM().cleanupSubmenu(2);
    }
}
