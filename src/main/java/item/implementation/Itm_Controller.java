package item.implementation;

import core.GamePanel;
import entity.EntityBase;
import item.ItemBase;
import utility.AssetPool;

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
        sprite = AssetPool.getSpritesheet(4).getSprite(1);
    }


    // METHOD
    @Override
    public boolean use(EntityBase user) {

        return false;
    }
}
