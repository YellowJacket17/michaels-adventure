package entity.implementation.character;

import asset.AssetPool;
import core.GamePanel;
import entity.EntityBase;
import entity.enumeration.EntityType;

/**
 * This class defines a character entity (Test3).
 */
public class Npc_Test3 extends EntityBase {

    // CONSTRUCTOR
    public Npc_Test3(GamePanel gp, int entityId) {
        super(gp, entityId, EntityType.CHARACTER);
        setupSprite();
    }


    // METHOD
    /**
     * Sets loaded entity sprites.
     */
    private void setupSprite() {

        idleDown = AssetPool.getSpritesheet("characters").getSprite(72);
        walkDown1 = AssetPool.getSpritesheet("characters").getSprite(73);
        walkDown2 = AssetPool.getSpritesheet("characters").getSprite(74);

        idleUp = AssetPool.getSpritesheet("characters").getSprite(75);
        walkUp1 = AssetPool.getSpritesheet("characters").getSprite(76);
        walkUp2 = AssetPool.getSpritesheet("characters").getSprite(77);

        idleLeft = AssetPool.getSpritesheet("characters").getSprite(78);
        walkLeft1 = AssetPool.getSpritesheet("characters").getSprite(79);
        walkLeft2 = AssetPool.getSpritesheet("characters").getSprite(80);

        idleRight = AssetPool.getSpritesheet("characters").getSprite(81);
        walkRight1 = AssetPool.getSpritesheet("characters").getSprite(82);
        walkRight2 = AssetPool.getSpritesheet("characters").getSprite(83);

        combatStanceLeft1 = AssetPool.getSpritesheet("characters").getSprite(84);
        combatStanceLeft2 = AssetPool.getSpritesheet("characters").getSprite(85);

        combatStanceRight1 = AssetPool.getSpritesheet("characters").getSprite(87);
        combatStanceRight2 = AssetPool.getSpritesheet("characters").getSprite(88);

        combatAttackLeft = AssetPool.getSpritesheet("characters").getSprite(86);
        combatAttackRight = AssetPool.getSpritesheet("characters").getSprite(89);

        sprite = idleDown;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
