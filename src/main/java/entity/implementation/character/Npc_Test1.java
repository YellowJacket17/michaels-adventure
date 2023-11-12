package entity.implementation.character;

import entity.EntityBase;
import entity.EntityType;
import core.GamePanel;
import utility.AssetPool;

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

        down1 = AssetPool.getSpritesheet(1).getSprite(12);
        down2 = AssetPool.getSpritesheet(1).getSprite(13);
        down3 = AssetPool.getSpritesheet(1).getSprite(14);

        up1 = AssetPool.getSpritesheet(1).getSprite(15);
        up2 = AssetPool.getSpritesheet(1).getSprite(16);
        up3 = AssetPool.getSpritesheet(1).getSprite(17);

        left1 = AssetPool.getSpritesheet(1).getSprite(18);
        left2 = AssetPool.getSpritesheet(1).getSprite(19);
        left3 = AssetPool.getSpritesheet(1).getSprite(20);

        right1 = AssetPool.getSpritesheet(1).getSprite(21);
        right2 = AssetPool.getSpritesheet(1).getSprite(22);
        right3 = AssetPool.getSpritesheet(1).getSprite(23);

        sprite = down1;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
