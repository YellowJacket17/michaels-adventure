package item.implementation;

import core.GamePanel;
import entity.EntityBase;
import item.ItemBase;

/**
 * This class defines an item (Controller).
 */
public class Itm_Controller extends ItemBase {

    // FIELDS
    private static final int itmId = 1;
    private static final String itmName = "Controller";
    private static final String itmDescription = "This controller invokes an odd feeling of nostalgia...";


    // CONSTRUCTOR
    public Itm_Controller(GamePanel gp) {
        super(gp, itmId, false);
        name = itmName;
        description = itmDescription;
        image = setupImage("controller.png");
    }


    // METHOD
    @Override
    public boolean use(EntityBase user) {

        return false;
    }
}
