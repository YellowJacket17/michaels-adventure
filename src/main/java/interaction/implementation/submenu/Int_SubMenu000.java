package interaction.implementation.submenu;

import core.GamePanel;
import interaction.InteractionSubMenuBase;

/**
 * This class implements interaction logic for sub-menu with ID 0.
 * Note that a sub-menu ID of 0 is used for resetting the camera when exiting debug mode.
 */
public class Int_SubMenu000 extends InteractionSubMenuBase {

    // CONSTRUCTOR
    public Int_SubMenu000(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run(int selectedIndex) {

        if (selectedIndex == 0) {

            gp.getCameraS().resetCameraSnap();
        }
        gp.getInteractionM().cleanupSubmenu(1);
    }
}
