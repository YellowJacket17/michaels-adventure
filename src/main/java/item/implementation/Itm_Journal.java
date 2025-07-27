package item.implementation;

import core.GamePanel;
import entity.EntityBase;
import item.ItemBase;
import asset.AssetPool;

/**
 * This class defines an item (Journal).
 */
public class Itm_Journal extends ItemBase {

    // FIELDS
    private static final int itmId = 5;
    private static final String itmName = "Journal";
    private static final String itmDescription = "A mysterious research journal chronicling encounters with otherworldly creatures. It looks like it may be part of a series.";


    // CONSTRUCTOR
    public Itm_Journal(GamePanel gp) {
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

        sprite = AssetPool.getSpritesheet("items").getSprite(5);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
