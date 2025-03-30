package event.implementation.submenu;

import core.GamePanel;
import event.EventSubMenuBase;

/**
 * This class implements post-selection event logic for sub-menu with ID 2.
 * Note that a sub-menu ID of 2 is used for resetting the camera when exiting debug mode.
 */
public class Evt_SubMenu002 extends EventSubMenuBase {

    // CONSTRUCTOR
    public Evt_SubMenu002(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run(int selectedIndex) {

        if (selectedIndex == 0) {

            gp.getCameraS().resetCameraSnap();
        }
        gp.getEventM().cleanupSubmenu(1);
    }
}
