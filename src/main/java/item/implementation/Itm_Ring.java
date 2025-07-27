package item.implementation;

import core.GamePanel;
import entity.EntityBase;
import item.ItemBase;
import asset.AssetPool;

/**
 * This class defines an item (Ring).
 */
public class Itm_Ring extends ItemBase {

    // FIELDS
    private static final int itmId = 4;
    private static final String itmName = "Ring";
    private static final String itmDescription = "Mary's engagement ring. The blue sapphire and surrounding diamonds sparkle softly against the light.";


    // CONSTRUCTOR
    public Itm_Ring(GamePanel gp) {
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

        sprite = AssetPool.getSpritesheet("items").getSprite(4);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
