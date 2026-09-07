package item.implementation;

import asset.AssetPool;
import core.GamePanel;
import entity.EntityBase;
import item.ItemBase;

/**
 * This class defines an item (Painting).
 */
public class Itm_Painting extends ItemBase {

    // FIELDS
    private static final int itmId = 10;
    private static final String itmName = "Painting";
    private static final String itmDescription = "A . . . thought-provoking painting, to put it mildly. Why would anyone throw this away?";


    // CONSTRUCTOR
    public Itm_Painting(GamePanel gp) {
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

        sprite = AssetPool.getSpritesheet("items").getSprite(7);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
