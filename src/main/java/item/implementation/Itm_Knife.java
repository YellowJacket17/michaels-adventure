package item.implementation;

import asset.AssetPool;
import core.GamePanel;
import entity.EntityBase;
import item.ItemBase;

/**
 * This class defines an item (Knife).
 */
public class Itm_Knife extends ItemBase {

    // FIELDS
    private static final int itmId = 7;
    private static final String itmName = "Pocket Knife";
    private static final String itmDescription = "A bizarre gift from Joe. The previous owner laser-engraved Kuromi onto the handle and brushed it purple.";


    // CONSTRUCTOR
    public Itm_Knife(GamePanel gp) {
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
