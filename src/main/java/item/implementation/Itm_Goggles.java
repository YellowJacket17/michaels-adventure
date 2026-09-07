package item.implementation;

import asset.AssetPool;
import core.GamePanel;
import entity.EntityBase;
import item.ItemBase;

/**
 * This class defines an item (Goggles).
 */
public class Itm_Goggles extends ItemBase {

    // FIELDS
    private static final int itmId = 9;
    private static final String itmName = "Swim Goggles";
    private static final String itmDescription = "Damp swim goggles that were cast to the ground. Hope no one tried to jump into the water . . .";


    // CONSTRUCTOR
    public Itm_Goggles(GamePanel gp) {
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
