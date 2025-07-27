package item.implementation;

import core.GamePanel;
import entity.EntityBase;
import item.ItemBase;
import asset.AssetPool;

/**
 * This class defines an item (Watch).
 */
public class Itm_Watch extends ItemBase {

    // FIELDS
    private static final int itmId = 2;
    private static final String itmName = "Watch";
    private static final String itmDescription = "Nick's moonphase watch. It looks like it needs to be wound, but is otherwise in great condition.";


    // CONSTRUCTOR
    public Itm_Watch(GamePanel gp) {
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

        sprite = AssetPool.getSpritesheet("items").getSprite(2);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
