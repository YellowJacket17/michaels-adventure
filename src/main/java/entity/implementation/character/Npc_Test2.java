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
    }


    // METHOD
    @Override
    protected void setSprites() {

        idleDown = AssetPool.getSpritesheet("characters").getSprite(36);
        walkDown1 = AssetPool.getSpritesheet("characters").getSprite(37);
        walkDown2 = AssetPool.getSpritesheet("characters").getSprite(38);

        idleUp = AssetPool.getSpritesheet("characters").getSprite(39);
        walkUp1 = AssetPool.getSpritesheet("characters").getSprite(40);
        walkUp2 = AssetPool.getSpritesheet("characters").getSprite(41);

        idleLeft = AssetPool.getSpritesheet("characters").getSprite(42);
        walkLeft1 = AssetPool.getSpritesheet("characters").getSprite(43);
        walkLeft2 = AssetPool.getSpritesheet("characters").getSprite(44);

        idleRight = AssetPool.getSpritesheet("characters").getSprite(45);
        walkRight1 = AssetPool.getSpritesheet("characters").getSprite(46);
        walkRight2 = AssetPool.getSpritesheet("characters").getSprite(47);

        combatStanceLeft1 = AssetPool.getSpritesheet("characters").getSprite(48);
        combatStanceLeft2 = AssetPool.getSpritesheet("characters").getSprite(49);

        combatStanceRight1 = AssetPool.getSpritesheet("characters").getSprite(51);
        combatStanceRight2 = AssetPool.getSpritesheet("characters").getSprite(52);

        combatAttackLeft = AssetPool.getSpritesheet("characters").getSprite(50);
        combatAttackRight = AssetPool.getSpritesheet("characters").getSprite(53);

        sprite = idleDown;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
