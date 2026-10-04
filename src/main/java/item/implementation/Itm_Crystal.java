package item.implementation;

import asset.AssetPool;
import core.GamePanel;
import entity.EntityBase;
import item.ItemBase;

/**
 * This class defines an item (Crystal).
 */
public class Itm_Crystal extends ItemBase {

    // FIELDS
    private static final int itmId = 8;
    private static final String itmName = "Crystal";
    private static final String itmDescription = "A beautiful blue crystal with strange emblem and a necklace latch. It emanates the refined power of the earth.";


    // CONSTRUCTOR
    public Itm_Crystal(GamePanel gp) {
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

        sprite = AssetPool.getSpritesheet("items").getSprite(8);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
