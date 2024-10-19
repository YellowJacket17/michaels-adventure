package item.implementation;

import core.GamePanel;
import entity.EntityBase;
import item.ItemBase;
import asset.AssetPool;

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
    }


    // METHODS
    @Override
    public boolean useField(EntityBase user) {

        return false;
    }


    @Override
    public boolean useCombat(EntityBase user) {

        return false;
    }


    @Override
    protected void setSprite() {

        sprite = AssetPool.getSpritesheet("items").getSprite(1);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
