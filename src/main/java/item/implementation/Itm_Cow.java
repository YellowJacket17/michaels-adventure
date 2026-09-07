package item.implementation;

import asset.AssetPool;
import core.GamePanel;
import entity.EntityBase;
import item.ItemBase;

/**
 * This class defines an item (Cow).
 */
public class Itm_Cow extends ItemBase {

    // FIELDS
    private static final int itmId = 11;
    private static final String itmName = "Highland Cow Plushie";
    private static final String itmDescription = "A plushie of the gentle highland cow. Hugging it seems to make all of the world's troubles melt away.";


    // CONSTRUCTOR
    public Itm_Cow(GamePanel gp) {
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
