package item.implementation;

import core.GamePanel;
import entity.EntityBase;
import item.ItemBase;
import asset.AssetPool;

/**
 * This class defines an item (Key).
 */
public class Itm_Key extends ItemBase {

    // FIELDS
    private static final int itmId = 0;
    private static final String itmName = "Key";
    private static final String itmDescription = "Who knows what secrets this could unlock?";


    // CONSTRUCTOR
    public Itm_Key(GamePanel gp) {
        super(gp, itmId, true);
        name = itmName;
        description = itmDescription;
        sprite = AssetPool.getSpritesheet("items").getSprite(0);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
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
}
