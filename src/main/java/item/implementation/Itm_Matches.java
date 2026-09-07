package item.implementation;

import asset.AssetPool;
import core.GamePanel;
import entity.EntityBase;
import item.ItemBase;

/**
 * This class defines an item (Matches).
 */
public class Itm_Matches extends ItemBase {

    // FIELDS
    private static final int itmId = 12;
    private static final String itmName = "Matches";
    private static final String itmDescription = "A box of standard wooden matches. Packed for the camping trip in case of emergency.";


    // CONSTRUCTOR
    public Itm_Matches(GamePanel gp) {
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
