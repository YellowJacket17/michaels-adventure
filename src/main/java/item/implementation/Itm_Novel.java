package item.implementation;

import core.GamePanel;
import entity.EntityBase;
import item.ItemBase;
import asset.AssetPool;

/**
 * This class defines an item (Novel).
 */
public class Itm_Novel extends ItemBase {

    // FIELDS
    private static final int itmId = 0;
    private static final String itmName = "Novel";
    private static final String itmDescription = "A worn hardcover book. Despite water damage, the word \"Schwab\" is legible on the cover.";


    // CONSTRUCTOR
    public Itm_Novel(GamePanel gp) {
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

        sprite = AssetPool.getSpritesheet("items").getSprite(0);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
