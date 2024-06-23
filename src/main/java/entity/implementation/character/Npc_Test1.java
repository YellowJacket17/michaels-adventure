package entity.implementation.character;

import entity.EntityBase;
import entity.enumeration.EntityType;
import core.GamePanel;
import asset.AssetPool;

/**
 * This class defines a character entity (Test1).
 */
public class Npc_Test1 extends EntityBase {

    // CONSTRUCTOR
    public Npc_Test1(GamePanel gp, int entityId) {
        super(gp, entityId, EntityType.CHARACTER);
        setupSprite();
    }


    // METHOD
    /**
     * Sets loaded entity sprites.
     */
    private void setupSprite() {

        down1 = AssetPool.getSpritesheet("characters").getSprite(90);
        down2 = AssetPool.getSpritesheet("characters").getSprite(91);
        down3 = AssetPool.getSpritesheet("characters").getSprite(92);

        up1 = AssetPool.getSpritesheet("characters").getSprite(93);
        up2 = AssetPool.getSpritesheet("characters").getSprite(94);
        up3 = AssetPool.getSpritesheet("characters").getSprite(95);

        left1 = AssetPool.getSpritesheet("characters").getSprite(96);
        left2 = AssetPool.getSpritesheet("characters").getSprite(97);
        left3 = AssetPool.getSpritesheet("characters").getSprite(98);

        right1 = AssetPool.getSpritesheet("characters").getSprite(99);
        right2 = AssetPool.getSpritesheet("characters").getSprite(100);
        right3 = AssetPool.getSpritesheet("characters").getSprite(101);

        sprite = down1;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
