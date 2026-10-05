package event.implementation.submenu;

import core.GamePanel;
import event.EventSubMenuBase;

/**
 * This class implements post-selection event logic for sub-menu with ID 5.
 * Note that a sub-menu ID of 5 is used for selecting a specific area of the game to load.
 */
public class Evt_SubMenu005 extends EventSubMenuBase {

    // CONSTRUCTOR
    public Evt_SubMenu005(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run(int selectedIndex) {

        gp.setTutorialsEnabled(false);

        if (selectedIndex == 0) {

            gp.getCutsceneM().initiateCutscene(1);
        } else if (selectedIndex == 1) {

            gp.getCutsceneM().initiateCutscene(9);
        } else {

            gp.getCutsceneM().initiateCutscene(10);
        }
        gp.getEventM().cleanupSubmenu(2);
    }
}
