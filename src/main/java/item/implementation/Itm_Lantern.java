package item.implementation;

import asset.AssetPool;
import core.GamePanel;
import entity.EntityBase;
import item.ItemBase;

/**
 * This class defines an item (Lantern).
 */
public class Itm_Lantern extends ItemBase {

    // FIELDS
    private static final int itmId = 6;
    private static final String itmName = "Lantern";
    private static final String itmDescription = "A battery-powered lantern with a classic look.";


    // CONSTRUCTOR
    public Itm_Lantern(GamePanel gp) {
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
