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

        idleDown = AssetPool.getSpritesheet("characters").getSprite(84);
        walkDown1 = AssetPool.getSpritesheet("characters").getSprite(85);
        walkDown2 = AssetPool.getSpritesheet("characters").getSprite(86);

        idleUp = AssetPool.getSpritesheet("characters").getSprite(87);
        walkUp1 = AssetPool.getSpritesheet("characters").getSprite(88);
        walkUp2 = AssetPool.getSpritesheet("characters").getSprite(89);

        idleLeft = AssetPool.getSpritesheet("characters").getSprite(90);
        walkLeft1 = AssetPool.getSpritesheet("characters").getSprite(91);
        walkLeft2 = AssetPool.getSpritesheet("characters").getSprite(92);

        idleRight = AssetPool.getSpritesheet("characters").getSprite(93);
        walkRight1 = AssetPool.getSpritesheet("characters").getSprite(94);
        walkRight2 = AssetPool.getSpritesheet("characters").getSprite(95);

        combatStanceLeft1 = AssetPool.getSpritesheet("characters").getSprite(96);
        combatStanceLeft2 = AssetPool.getSpritesheet("characters").getSprite(97);

        combatStanceRight1 = AssetPool.getSpritesheet("characters").getSprite(99);
        combatStanceRight2 = AssetPool.getSpritesheet("characters").getSprite(100);

        combatAttackLeft = AssetPool.getSpritesheet("characters").getSprite(98);
        combatAttackRight = AssetPool.getSpritesheet("characters").getSprite(101);

        combatFaintLeft1_1 = AssetPool.getSpritesheet("characters").getSprite(102);
        combatFaintLeft2_1 = AssetPool.getSpritesheet("characters").getSprite(104);
        combatFaintLeft3_1 = AssetPool.getSpritesheet("characters").getSprite(106);
        combatFaintLeft4_1 = AssetPool.getSpritesheet("characters").getSprite(108);
        combatFaintLeft5_1 = AssetPool.getSpritesheet("characters").getSprite(110);
        combatFaintLeft6_1 = AssetPool.getSpritesheet("characters").getSprite(112);

        combatFaintLeft1_2 = AssetPool.getSpritesheet("characters").getSprite(103);
        combatFaintLeft2_2 = AssetPool.getSpritesheet("characters").getSprite(105);
        combatFaintLeft3_2 = AssetPool.getSpritesheet("characters").getSprite(107);
        combatFaintLeft4_2 = AssetPool.getSpritesheet("characters").getSprite(109);
        combatFaintLeft5_2 = AssetPool.getSpritesheet("characters").getSprite(111);
        combatFaintLeft6_2 = AssetPool.getSpritesheet("characters").getSprite(113);

        combatFaintRight1_1 = AssetPool.getSpritesheet("characters").getSprite(114);
        combatFaintRight2_1 = AssetPool.getSpritesheet("characters").getSprite(116);
        combatFaintRight3_1 = AssetPool.getSpritesheet("characters").getSprite(118);
        combatFaintRight4_1 = AssetPool.getSpritesheet("characters").getSprite(120);
        combatFaintRight5_1 = AssetPool.getSpritesheet("characters").getSprite(122);
        combatFaintRight6_1 = AssetPool.getSpritesheet("characters").getSprite(124);

        combatFaintRight1_2 = AssetPool.getSpritesheet("characters").getSprite(115);
        combatFaintRight2_2 = AssetPool.getSpritesheet("characters").getSprite(117);
        combatFaintRight3_2 = AssetPool.getSpritesheet("characters").getSprite(119);
        combatFaintRight4_2 = AssetPool.getSpritesheet("characters").getSprite(121);
        combatFaintRight5_2 = AssetPool.getSpritesheet("characters").getSprite(123);
        combatFaintRight6_2 = AssetPool.getSpritesheet("characters").getSprite(125);

        sprite = idleDown;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
