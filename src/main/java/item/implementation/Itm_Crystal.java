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
    private static final String itmDescription = "A beautiful blue crystal strung as a necklace. It emanates the refined power of the Earth.";


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

        sprite = AssetPool.getSpritesheet("items").getSprite(6);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
