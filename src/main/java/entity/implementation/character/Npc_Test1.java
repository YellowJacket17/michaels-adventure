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
    }


    // METHOD
    @Override
    protected void setSprites() {

        idleDown = AssetPool.getSpritesheet("characters").getSprite(90);
        walkDown1 = AssetPool.getSpritesheet("characters").getSprite(91);
        walkDown2 = AssetPool.getSpritesheet("characters").getSprite(92);

        idleUp = AssetPool.getSpritesheet("characters").getSprite(93);
        walkUp1 = AssetPool.getSpritesheet("characters").getSprite(94);
        walkUp2 = AssetPool.getSpritesheet("characters").getSprite(95);

        idleLeft = AssetPool.getSpritesheet("characters").getSprite(96);
        walkLeft1 = AssetPool.getSpritesheet("characters").getSprite(97);
        walkLeft2 = AssetPool.getSpritesheet("characters").getSprite(98);

        idleRight = AssetPool.getSpritesheet("characters").getSprite(99);
        walkRight1 = AssetPool.getSpritesheet("characters").getSprite(100);
        walkRight2 = AssetPool.getSpritesheet("characters").getSprite(101);

        combatStanceLeft1 = AssetPool.getSpritesheet("characters").getSprite(102);
        combatStanceLeft2 = AssetPool.getSpritesheet("characters").getSprite(103);

        combatStanceRight1 = AssetPool.getSpritesheet("characters").getSprite(105);
        combatStanceRight2 = AssetPool.getSpritesheet("characters").getSprite(106);

        combatAttackLeft = AssetPool.getSpritesheet("characters").getSprite(104);
        combatAttackRight = AssetPool.getSpritesheet("characters").getSprite(107);

        combatFaintLeft1 = AssetPool.getSpritesheet("characters").getSprite(117);
        combatFaintLeft2 = AssetPool.getSpritesheet("characters").getSprite(118);
        combatFaintLeft3 = AssetPool.getSpritesheet("characters").getSprite(119);
        combatFaintLeft4 = AssetPool.getSpritesheet("characters").getSprite(120);
        combatFaintLeft5 = AssetPool.getSpritesheet("characters").getSprite(121);

        combatFaintRight1 = AssetPool.getSpritesheet("characters").getSprite(117);
        combatFaintRight2 = AssetPool.getSpritesheet("characters").getSprite(118);
        combatFaintRight3 = AssetPool.getSpritesheet("characters").getSprite(119);
        combatFaintRight4 = AssetPool.getSpritesheet("characters").getSprite(120);
        combatFaintRight5 = AssetPool.getSpritesheet("characters").getSprite(121);

        sprite = idleDown;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
