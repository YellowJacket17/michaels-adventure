package entity.implementation.character;

import entity.EntityBase;
import entity.enumeration.EntityType;
import core.GamePanel;
import asset.AssetPool;

/**
 * This class defines a character entity (Test2).
 */
public class Npc_Test2 extends EntityBase {

    // CONSTRUCTOR
    public Npc_Test2(GamePanel gp, int entityId) {
        super(gp, entityId, EntityType.CHARACTER);
        setupSprite();
    }


    // METHOD
    /**
     * Sets loaded entity sprites.
     */
    private void setupSprite() {

        down1 = AssetPool.getSpritesheet("characters").getSprite(36);
        down2 = AssetPool.getSpritesheet("characters").getSprite(37);
        down3 = AssetPool.getSpritesheet("characters").getSprite(38);

        up1 = AssetPool.getSpritesheet("characters").getSprite(39);
        up2 = AssetPool.getSpritesheet("characters").getSprite(40);
        up3 = AssetPool.getSpritesheet("characters").getSprite(41);

        left1 = AssetPool.getSpritesheet("characters").getSprite(42);
        left2 = AssetPool.getSpritesheet("characters").getSprite(43);
        left3 = AssetPool.getSpritesheet("characters").getSprite(44);

        right1 = AssetPool.getSpritesheet("characters").getSprite(45);
        right2 = AssetPool.getSpritesheet("characters").getSprite(46);
        right3 = AssetPool.getSpritesheet("characters").getSprite(47);

        sprite = down1;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
