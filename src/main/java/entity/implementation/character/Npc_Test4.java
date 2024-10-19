package entity.implementation.character;

import asset.AssetPool;
import core.GamePanel;
import entity.EntityBase;
import entity.enumeration.EntityType;

/**
 * This class defines a character entity (Test4).
 */
public class Npc_Test4 extends EntityBase {

    // CONSTRUCTOR
    public Npc_Test4(GamePanel gp, int entityId) {
        super(gp, entityId, EntityType.CHARACTER);
    }


    // METHOD
    @Override
    protected void setSprites() {

        idleDown = AssetPool.getSpritesheet("characters").getSprite(54);
        walkDown1 = AssetPool.getSpritesheet("characters").getSprite(55);
        walkDown2 = AssetPool.getSpritesheet("characters").getSprite(56);

        idleUp = AssetPool.getSpritesheet("characters").getSprite(57);
        walkUp1 = AssetPool.getSpritesheet("characters").getSprite(58);
        walkUp2 = AssetPool.getSpritesheet("characters").getSprite(59);

        idleLeft = AssetPool.getSpritesheet("characters").getSprite(60);
        walkLeft1 = AssetPool.getSpritesheet("characters").getSprite(61);
        walkLeft2 = AssetPool.getSpritesheet("characters").getSprite(62);

        idleRight = AssetPool.getSpritesheet("characters").getSprite(63);
        walkRight1 = AssetPool.getSpritesheet("characters").getSprite(64);
        walkRight2 = AssetPool.getSpritesheet("characters").getSprite(65);

        combatStanceLeft1 = AssetPool.getSpritesheet("characters").getSprite(66);
        combatStanceLeft2 = AssetPool.getSpritesheet("characters").getSprite(67);

        combatStanceRight1 = AssetPool.getSpritesheet("characters").getSprite(69);
        combatStanceRight2 = AssetPool.getSpritesheet("characters").getSprite(70);

        combatAttackLeft = AssetPool.getSpritesheet("characters").getSprite(68);
        combatAttackRight = AssetPool.getSpritesheet("characters").getSprite(71);

        sprite = idleDown;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
