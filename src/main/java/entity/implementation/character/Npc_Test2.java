package entity.implementation.character;

import entity.EntityBase;
import entity.EntityType;
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

        down1 = AssetPool.getSpritesheet(1).getSprite(0);
        down2 = AssetPool.getSpritesheet(1).getSprite(1);
        down3 = AssetPool.getSpritesheet(1).getSprite(2);

        up1 = AssetPool.getSpritesheet(1).getSprite(3);
        up2 = AssetPool.getSpritesheet(1).getSprite(4);
        up3 = AssetPool.getSpritesheet(1).getSprite(5);

        left1 = AssetPool.getSpritesheet(1).getSprite(6);
        left2 = AssetPool.getSpritesheet(1).getSprite(7);
        left3 = AssetPool.getSpritesheet(1).getSprite(8);

        right1 = AssetPool.getSpritesheet(1).getSprite(9);
        right2 = AssetPool.getSpritesheet(1).getSprite(10);
        right3 = AssetPool.getSpritesheet(1).getSprite(11);

        sprite = down1;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
